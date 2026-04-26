import Foundation

struct Activity: Codable {
    let id: Int?
    var name: String
    var type: String?
    var location: String?
    var startDate: String?
    var duration: String?
    var description: String?
    var totalDistance: Double?
    var totalElevation: Int?
    var coverIcon: String?
    var userId: Int?
    var favorite: Bool?
    var createdAt: String?
    var updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, name, type, location, duration, description
        case totalDistance = "total_distance"
        case totalElevation = "total_elevation"
        case coverIcon = "cover_icon"
        case userId = "user_id"
        case favorite
        case createdAt = "created_at"
        case updatedAt = "updated_at"
        case startDate = "start_date"
    }
}

struct ActivityCreateReq: Codable {
    let name: String
    let type: String?
    let location: String?
    let startDate: String?
    let duration: String?
    let description: String?

    enum CodingKeys: String, CodingKey {
        case name, type, location, duration, description
        case startDate = "start_date"
    }
}
