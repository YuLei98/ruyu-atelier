import Foundation

struct Equipment: Codable {
    let id: Int?
    var name: String
    var category: String?
    var icon: String?
    var quantity: Int?
    var remark: String?
    var userId: Int?
    var createdAt: String?
    var updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, name, category, icon, quantity, remark
        case userId = "user_id"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}

struct EquipmentCreateReq: Codable {
    let name: String
    var category: String?
    var icon: String?
    var quantity: Int?
    var remark: String?

    enum CodingKeys: String, CodingKey {
        case name, category, icon, quantity, remark
    }

    init(name: String, category: String? = nil, icon: String? = nil, quantity: Int? = nil, remark: String? = nil) {
        self.name = name
        self.category = category
        self.icon = icon
        self.quantity = quantity
        self.remark = remark
    }
}
