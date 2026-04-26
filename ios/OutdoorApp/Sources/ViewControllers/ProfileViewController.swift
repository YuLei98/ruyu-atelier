import UIKit

class ProfileViewController: UIViewController {

    private var partners: [Partner] = []
    private let tableView = UITableView()
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadPartners()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadPartners()
    }

    private func setupUI() {
        title = "我的"
        view.backgroundColor = UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)

        navigationController?.navigationBar.prefersLargeTitles = true

        let addButton = UIBarButtonItem(image: UIImage(systemName: "plus"), style: .plain, target: self, action: #selector(addPartner))
        addButton.tintColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        navigationItem.rightBarButtonItem = addButton

        tableView.frame = view.bounds
        tableView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        tableView.backgroundColor = .clear
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(PartnerCell.self, forCellReuseIdentifier: "PartnerCell")
        view.addSubview(tableView)

        refreshControl.addTarget(self, action: #selector(loadPartners), for: .valueChanged)
        tableView.refreshControl = refreshControl
    }

    @objc private func loadPartners() {
        APIService.shared.getPartners { [weak self] result in
            DispatchQueue.main.async {
                self?.refreshControl.endRefreshing()
                switch result {
                case .success(let partners):
                    self?.partners = partners
                    self?.tableView.reloadData()
                case .failure(let error):
                    self?.showAlert(title: "错误", message: error.localizedDescription)
                }
            }
        }
    }

    @objc private func addPartner() {
        let alertController = UIAlertController(title: "添加队友", message: nil, preferredStyle: .alert)

        alertController.addTextField { textField in
            textField.placeholder = "队友姓名"
        }

        alertController.addTextField { textField in
            textField.placeholder = "联系方式"
        }

        alertController.addTextField { textField in
            textField.placeholder = "备注"
        }

        alertController.addAction(UIAlertAction(title: "取消", style: .cancel))
        alertController.addAction(UIAlertAction(title: "添加", style: .default) { [weak self] _ in
            guard let name = alertController.textFields?[0].text, !name.isEmpty else {
                return
            }
            let contact = alertController.textFields?[1].text
            let remark = alertController.textFields?[2].text

            let req = PartnerCreateReq(name: name, contact: contact, remark: remark)
            APIService.shared.createPartner(req) { result in
                DispatchQueue.main.async {
                    switch result {
                    case .success:
                        self?.loadPartners()
                    case .failure(let error):
                        self?.showAlert(title: "错误", message: error.localizedDescription)
                    }
                }
            }
        })

        present(alertController, animated: true)
    }

    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "确定", style: .default))
        present(alert, animated: true)
    }
}

extension ProfileViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return partners.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PartnerCell", for: indexPath) as! PartnerCell
        cell.configure(with: partners[indexPath.row])
        return cell
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 72
    }
}

class PartnerCell: UITableViewCell {

    private let containerView = UIView()
    private let avatarLabel = UILabel()
    private let nameLabel = UILabel()
    private let contactLabel = UILabel()
    private let remarkLabel = UILabel()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUI()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupUI() {
        backgroundColor = .clear
        selectionStyle = .none

        containerView.backgroundColor = .white
        containerView.layer.cornerRadius = 12
        containerView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(containerView)

        avatarLabel.text = "👤"
        avatarLabel.font = .systemFont(ofSize: 28)
        avatarLabel.textAlignment = .center
        avatarLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(avatarLabel)

        nameLabel.font = .systemFont(ofSize: 16, weight: .medium)
        nameLabel.textColor = UIColor(red: 30/255, green: 41/255, blue: 59/255, alpha: 1)
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(nameLabel)

        contactLabel.font = .systemFont(ofSize: 13)
        contactLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        contactLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(contactLabel)

        remarkLabel.font = .systemFont(ofSize: 12)
        remarkLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        remarkLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(remarkLabel)

        NSLayoutConstraint.activate([
            containerView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 4),
            containerView.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 16),
            containerView.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -16),
            containerView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -4),

            avatarLabel.leftAnchor.constraint(equalTo: containerView.leftAnchor, constant: 16),
            avatarLabel.centerYAnchor.constraint(equalTo: containerView.centerYAnchor),
            avatarLabel.widthAnchor.constraint(equalToConstant: 40),

            nameLabel.topAnchor.constraint(equalTo: containerView.topAnchor, constant: 14),
            nameLabel.leftAnchor.constraint(equalTo: avatarLabel.rightAnchor, constant: 12),
            nameLabel.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -16),

            contactLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 4),
            contactLabel.leftAnchor.constraint(equalTo: avatarLabel.rightAnchor, constant: 12),

            remarkLabel.topAnchor.constraint(equalTo: contactLabel.bottomAnchor, constant: 2),
            remarkLabel.leftAnchor.constraint(equalTo: avatarLabel.rightAnchor, constant: 12),
            remarkLabel.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -16)
        ])
    }

    func configure(with partner: Partner) {
        nameLabel.text = partner.name
        contactLabel.text = partner.contact ?? "无联系方式"
        remarkLabel.text = partner.remark ?? ""
    }
}
