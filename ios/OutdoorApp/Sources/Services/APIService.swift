import Foundation

struct APIResponse<T: Codable>: Codable {
    let code: Int
    let message: String
    let data: T?
}

class APIService {
    static let shared = APIService()

    private let baseURL = "http://localhost:8000/api"
    private let userId = 1

    private init() {}

    // MARK: - Activity

    func getActivities(completion: @escaping (Result<[Activity], Error>) -> Void) {
        let url = URL(string: "\(baseURL)/activities")!
        var request = URLRequest(url: url)
        request.setValue("\(userId)", forHTTPHeaderField: "X-User-Id")

        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else { return }
            do {
                let response = try JSONDecoder().decode(APIResponse<[Activity]>.self, from: data)
                completion(.success(response.data ?? []))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    func createActivity(_ req: ActivityCreateReq, completion: @escaping (Result<Activity, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/activities")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("\(userId)", forHTTPHeaderField: "X-User-Id")
        request.httpBody = try? JSONEncoder().encode(req)

        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else { return }
            do {
                let response = try JSONDecoder().decode(APIResponse<Activity>.self, from: data)
                if let activity = response.data {
                    completion(.success(activity))
                } else {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: response.message])))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    func deleteActivity(id: Int, completion: @escaping (Result<Void, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/activities/\(id)")!
        var request = URLRequest(url: url)
        request.httpMethod = "DELETE"

        URLSession.shared.dataTask(with: request) { _, _, error in
            if let error = error {
                completion(.failure(error))
            } else {
                completion(.success(()))
            }
        }.resume()
    }

    // MARK: - Track Point

    func addTrackPoint(_ req: TrackPointReq, completion: @escaping (Result<TrackPoint, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/activities/\(req.activityId ?? 0)/track")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try? JSONEncoder().encode(req)

        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else { return }
            do {
                let response = try JSONDecoder().decode(APIResponse<TrackPoint>.self, from: data)
                if let point = response.data {
                    completion(.success(point))
                } else {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: response.message])))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    // MARK: - Equipment

    func getEquipments(completion: @escaping (Result<[Equipment], Error>) -> Void) {
        let url = URL(string: "\(baseURL)/equipments")!
        var request = URLRequest(url: url)
        request.setValue("\(userId)", forHTTPHeaderField: "X-User-Id")

        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else { return }
            do {
                let response = try JSONDecoder().decode(APIResponse<[Equipment]>.self, from: data)
                completion(.success(response.data ?? []))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    func createEquipment(_ req: EquipmentCreateReq, completion: @escaping (Result<Equipment, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/equipments")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("\(userId)", forHTTPHeaderField: "X-User-Id")
        request.httpBody = try? JSONEncoder().encode(req)

        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else { return }
            do {
                let response = try JSONDecoder().decode(APIResponse<Equipment>.self, from: data)
                if let equipment = response.data {
                    completion(.success(equipment))
                } else {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: response.message])))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    // MARK: - Partner

    func getPartners(completion: @escaping (Result<[Partner], Error>) -> Void) {
        let url = URL(string: "\(baseURL)/partners")!
        var request = URLRequest(url: url)
        request.setValue("\(userId)", forHTTPHeaderField: "X-User-Id")

        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else { return }
            do {
                let response = try JSONDecoder().decode(APIResponse<[Partner]>.self, from: data)
                completion(.success(response.data ?? []))
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }

    func createPartner(_ req: PartnerCreateReq, completion: @escaping (Result<Partner, Error>) -> Void) {
        let url = URL(string: "\(baseURL)/partners")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("\(userId)", forHTTPHeaderField: "X-User-Id")
        request.httpBody = try? JSONEncoder().encode(req)

        URLSession.shared.dataTask(with: request) { data, _, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            guard let data = data else { return }
            do {
                let response = try JSONDecoder().decode(APIResponse<Partner>.self, from: data)
                if let partner = response.data {
                    completion(.success(partner))
                } else {
                    completion(.failure(NSError(domain: "", code: -1, userInfo: [NSLocalizedDescriptionKey: response.message])))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
}
