import UIKit

class EquipmentViewController: UIViewController {

    private var equipments: [Equipment] = []
    private let tableView = UITableView()
    private let refreshControl = UIRefreshControl()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        loadEquipments()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        loadEquipments()
    }

    private func setupUI() {
        title = "装备"
        view.backgroundColor = UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)

        navigationController?.navigationBar.prefersLargeTitles = true

        let addButton = UIBarButtonItem(image: UIImage(systemName: "plus"), style: .plain, target: self, action: #selector(addEquipment))
        addButton.tintColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        navigationItem.rightBarButtonItem = addButton

        tableView.frame = view.bounds
        tableView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        tableView.backgroundColor = .clear
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(EquipmentCell.self, forCellReuseIdentifier: "EquipmentCell")
        view.addSubview(tableView)

        refreshControl.addTarget(self, action: #selector(loadEquipments), for: .valueChanged)
        tableView.refreshControl = refreshControl
    }

    @objc private func loadEquipments() {
        APIService.shared.getEquipments { [weak self] result in
            DispatchQueue.main.async {
                self?.refreshControl.endRefreshing()
                switch result {
                case .success(let equipments):
                    self?.equipments = equipments
                    self?.tableView.reloadData()
                case .failure(let error):
                    self?.showAlert(title: "错误", message: error.localizedDescription)
                }
            }
        }
    }

    @objc private func addEquipment() {
        let alertController = UIAlertController(title: "添加装备", message: nil, preferredStyle: .alert)

        alertController.addTextField { textField in
            textField.placeholder = "装备名称"
        }

        alertController.addTextField { textField in
            textField.placeholder = "数量"
            textField.keyboardType = .numberPad
        }

        alertController.addTextField { textField in
            textField.placeholder = "备注"
        }

        alertController.addAction(UIAlertAction(title: "取消", style: .cancel))
        alertController.addAction(UIAlertAction(title: "添加", style: .default) { [weak self] _ in
            guard let name = alertController.textFields?[0].text, !name.isEmpty,
                  let quantityText = alertController.textFields?[1].text,
                  let quantity = Int(quantityText) else {
                return
            }
            let remark = alertController.textFields?[2].text

            let req = EquipmentCreateReq(name: name, quantity: quantity, remark: remark)
            APIService.shared.createEquipment(req) { result in
                DispatchQueue.main.async {
                    switch result {
                    case .success:
                        self?.loadEquipments()
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

extension EquipmentViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return equipments.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "EquipmentCell", for: indexPath) as! EquipmentCell
        cell.configure(with: equipments[indexPath.row])
        return cell
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
    }
}

class EquipmentCell: UITableViewCell {

    private let containerView = UIView()
    private let iconLabel = UILabel()
    private let nameLabel = UILabel()
    private let quantityLabel = UILabel()
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

        iconLabel.text = "🎒"
        iconLabel.font = .systemFont(ofSize: 32)
        iconLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(iconLabel)

        nameLabel.font = .systemFont(ofSize: 16, weight: .medium)
        nameLabel.textColor = UIColor(red: 30/255, green: 41/255, blue: 59/255, alpha: 1)
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(nameLabel)

        quantityLabel.font = .systemFont(ofSize: 14)
        quantityLabel.textColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        quantityLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(quantityLabel)

        remarkLabel.font = .systemFont(ofSize: 12)
        remarkLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        remarkLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(remarkLabel)

        NSLayoutConstraint.activate([
            containerView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 4),
            containerView.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 16),
            containerView.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -16),
            containerView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -4),

            iconLabel.leftAnchor.constraint(equalTo: containerView.leftAnchor, constant: 16),
            iconLabel.centerYAnchor.constraint(equalTo: containerView.centerYAnchor),

            nameLabel.topAnchor.constraint(equalTo: containerView.topAnchor, constant: 16),
            nameLabel.leftAnchor.constraint(equalTo: iconLabel.rightAnchor, constant: 12),
            nameLabel.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -16),

            quantityLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 4),
            quantityLabel.leftAnchor.constraint(equalTo: iconLabel.rightAnchor, constant: 12),

            remarkLabel.topAnchor.constraint(equalTo: quantityLabel.bottomAnchor, constant: 2),
            remarkLabel.leftAnchor.constraint(equalTo: iconLabel.rightAnchor, constant: 12),
            remarkLabel.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -16)
        ])
    }

    func configure(with equipment: Equipment) {
        nameLabel.text = equipment.name
        quantityLabel.text = "x\(equipment.quantity ?? 1)"
        remarkLabel.text = equipment.remark ?? ""
    }
}
