import UIKit

class HomeViewController: UIViewController {

    private var activities: [Activity] = []
    private let tableView = UITableView()
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadActivities()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadActivities()
    }

    private func setupUI() {
        title = "山野记"
        view.backgroundColor = UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)

        navigationController?.navigationBar.prefersLargeTitles = true

        let addButton = UIBarButtonItem(image: UIImage(systemName: "plus"), style: .plain, target: self, action: #selector(addActivity))
        addButton.tintColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        navigationItem.rightBarButtonItem = addButton

        tableView.frame = view.bounds
        tableView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        tableView.backgroundColor = .clear
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(ActivityCell.self, forCellReuseIdentifier: "ActivityCell")
        view.addSubview(tableView)

        refreshControl.addTarget(self, action: #selector(loadActivities), for: .valueChanged)
        tableView.refreshControl = refreshControl
    }

    @objc private func loadActivities() {
        APIService.shared.getActivities { [weak self] result in
            DispatchQueue.main.async {
                self?.refreshControl.endRefreshing()
                switch result {
                case .success(let activities):
                    self?.activities = activities
                    self?.tableView.reloadData()
                case .failure(let error):
                    self?.showAlert(title: "错误", message: error.localizedDescription)
                }
            }
        }
    }

    @objc private func addActivity() {
        let createVC = CreateActivityViewController()
        createVC.onComplete = { [weak self] in
            self?.loadActivities()
        }
        let navVC = UINavigationController(rootViewController: createVC)
        present(navVC, animated: true)
    }

    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "确定", style: .default))
        present(alert, animated: true)
    }
}

extension HomeViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return activities.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ActivityCell", for: indexPath) as! ActivityCell
        cell.configure(with: activities[indexPath.row])
        return cell
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 280
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let detailVC = ActivityDetailViewController(activity: activities[indexPath.row])
        navigationController?.pushViewController(detailVC, animated: true)
    }
}
