import Foundation

struct TrackPoint: Codable {
    let id: Int?
    let activityId: Int?
    let latitude: Double
    let longitude: Double
    let altitude: Double?
    let speed: Double?
    let timestamp: String?
    let createdAt: String?

    enum CodingKeys: String, CodingKey {
        case id, latitude, longitude, altitude, speed, timestamp
        case activityId = "activity_id"
        case createdAt = "created_at"
    }
}

struct TrackPointReq: Codable {
    let activityId: Int?
    let latitude: Double
    let longitude: Double
    let altitude: Double?
    let speed: Double?

    enum CodingKeys: String, CodingKey {
        case latitude, longitude, altitude, speed
        case activityId = "activity_id"
    }
}
