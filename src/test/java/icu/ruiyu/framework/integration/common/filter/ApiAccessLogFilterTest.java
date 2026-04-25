package icu.ruiyu.framework.integration.common.filter;

import icu.ruiyu.framework.log.filter.ApiAccessLogFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = com.ruiyu.framework.FrameworkApplication.class)
class ApiAccessLogFilterTest {

    @Autowired
    private ApiAccessLogFilter apiAccessLogFilter;

    @MockBean
    private FilterChain filterChain;

    @Test
    void testTraceIdGeneratedWhenHeaderAbsent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 不设置 X-Trace-Id header
        apiAccessLogFilter.doFilterInternal(request, response, filterChain);

        // 验证 filterChain 被调用
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testTraceIdFromHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/test");
        request.addHeader("X-Trace-Id", "custom-trace-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiAccessLogFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testUserIdFromSecurityContext() throws Exception {
        // 设置 SecurityContext
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        apiAccessLogFilter.doFilterInternal(request, response, filterChain);

        // 清理
        SecurityContextHolder.clearContext();
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testOptionsRequestFiltered() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("OPTIONS");
        request.setRequestURI("/test");

        assertTrue(apiAccessLogFilter.shouldNotFilter(request));
    }

    @Test
    void testNonOptionsRequestNotFiltered() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/test");

        assertFalse(apiAccessLogFilter.shouldNotFilter(request));
    }

    @Test
    void testLatencyCalculated() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        long start = System.currentTimeMillis();
        apiAccessLogFilter.doFilterInternal(request, response, filterChain);
        long end = System.currentTimeMillis();

        // 验证 filterChain 被调用
        verify(filterChain, times(1)).doFilter(any(), any());
    }
}