import Foundation

struct Partner: Codable {
    let id: Int?
    var name: String
    var avatarColor: String?
    var contact: String?
    var remark: String?
    var userId: Int?
    var createdAt: String?
    var updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, name
        case avatarColor = "avatar_color"
        case contact, remark
        case userId = "user_id"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}

struct PartnerCreateReq: Codable {
    let name: String
    var avatarColor: String?
    var contact: String?
    var remark: String?

    enum CodingKeys: String, CodingKey {
        case name
        case avatarColor = "avatar_color"
        case contact, remark
    }

    init(name: String, avatarColor: String? = nil, contact: String? = nil, remark: String? = nil) {
        self.name = name
        self.avatarColor = avatarColor
        self.contact = contact
        self.remark = remark
    }
}
