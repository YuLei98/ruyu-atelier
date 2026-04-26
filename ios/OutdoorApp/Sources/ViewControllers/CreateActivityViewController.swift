import UIKit

class CreateActivityViewController: UIViewController {

    var onComplete: (() -> Void)?

    private let scrollView = UIScrollView()
    private let contentView = UIView()

    private let nameTextField = UITextField()
    private let typeSegmentedControl = UISegmentedControl(items: ["徒步", "露营", "登山", "骑行"])
    private let locationTextField = UITextField()
    private let dateTextField = UITextField()
    private let durationTextField = UITextField()
    private let descriptionTextView = UITextView()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }

    private func setupUI() {
        title = "创建活动"
        view.backgroundColor = UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)

        navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel, target: self, action: #selector(cancelTapped))
        navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .save, target: self, action: #selector(saveTapped))

        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)

        contentView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(contentView)

        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: view.topAnchor),
            scrollView.leftAnchor.constraint(equalTo: view.leftAnchor),
            scrollView.rightAnchor.constraint(equalTo: view.rightAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            contentView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            contentView.leftAnchor.constraint(equalTo: scrollView.leftAnchor),
            contentView.rightAnchor.constraint(equalTo: scrollView.rightAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            contentView.widthAnchor.constraint(equalTo: scrollView.widthAnchor)
        ])

        setupForm()
    }

    private func setupForm() {
        let formStackView = UIStackView()
        formStackView.axis = .vertical
        formStackView.spacing = 16
        formStackView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(formStackView)

        // 名称
        let nameSection = createFormSection(title: "活动名称", textField: nameTextField, placeholder: "例如：周末爬山")
        formStackView.addArrangedSubview(nameSection)

        // 类型
        let typeSection = createSectionTitle(title: "活动类型")
        typeSegmentedControl.selectedSegmentIndex = 0
        typeSection.addArrangedSubview(typeSegmentedControl)
        formStackView.addArrangedSubview(typeSection)

        // 位置
        let locationSection = createFormSection(title: "位置", textField: locationTextField, placeholder: "例如：北京西山")
        formStackView.addArrangedSubview(locationSection)

        // 日期
        let dateSection = createFormSection(title: "日期", textField: dateTextField, placeholder: "例如：2024-01-15")
        formStackView.addArrangedSubview(dateSection)

        // 时长
        let durationSection = createFormSection(title: "时长", textField: durationTextField, placeholder: "例如：3小时")
        formStackView.addArrangedSubview(durationSection)

        // 描述
        let descLabel = UILabel()
        descLabel.text = "描述"
        descLabel.font = .systemFont(ofSize: 14, weight: .medium)
        descLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)

        descriptionTextView.font = .systemFont(ofSize: 16)
        descriptionTextView.layer.cornerRadius = 8
        descriptionTextView.layer.borderColor = UIColor.systemGray4.cgColor
        descriptionTextView.layer.borderWidth = 1
        descriptionTextView.backgroundColor = .white
        descriptionTextView.isScrollEnabled = false
        descriptionTextView.translatesAutoresizingMaskIntoConstraints = false

        let descContainer = UIView()
        descContainer.addSubview(descLabel)
        descContainer.addSubview(descriptionTextView)

        NSLayoutConstraint.activate([
            descLabel.topAnchor.constraint(equalTo: descContainer.topAnchor),
            descLabel.leftAnchor.constraint(equalTo: descContainer.leftAnchor),
            descLabel.rightAnchor.constraint(equalTo: descContainer.rightAnchor),
            descriptionTextView.topAnchor.constraint(equalTo: descLabel.bottomAnchor, constant: 8),
            descriptionTextView.leftAnchor.constraint(equalTo: descContainer.leftAnchor),
            descriptionTextView.rightAnchor.constraint(equalTo: descContainer.rightAnchor),
            descriptionTextView.heightAnchor.constraint(equalToConstant: 100),
            descriptionTextView.bottomAnchor.constraint(equalTo: descContainer.bottomAnchor)
        ])
        formStackView.addArrangedSubview(descContainer)

        NSLayoutConstraint.activate([
            formStackView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 20),
            formStackView.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 20),
            formStackView.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -20),
            formStackView.bottomAnchor.constraint(lessThanOrEqualTo: contentView.bottomAnchor, constant: -20)
        ])
    }

    private func createFormSection(title: String, textField: UITextField, placeholder: String) -> UIStackView {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 8

        let label = UILabel()
        label.text = title
        label.font = .systemFont(ofSize: 14, weight: .medium)
        label.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)

        textField.placeholder = placeholder
        textField.font = .systemFont(ofSize: 16)
        textField.borderStyle = .roundedRect
        textField.backgroundColor = .white
        textField.translatesAutoresizingMaskIntoConstraints = false

        stackView.addArrangedSubview(label)
        stackView.addArrangedSubview(textField)

        return stackView
    }

    private func createSectionTitle(title: String) -> UIStackView {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 8

        let label = UILabel()
        label.text = title
        label.font = .systemFont(ofSize: 14, weight: .medium)
        label.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)

        stackView.addArrangedSubview(label)

        return stackView
    }

    @objc private func cancelTapped() {
        dismiss(animated: true)
    }

    @objc private func saveTapped() {
        guard let name = nameTextField.text, !name.isEmpty else {
            showAlert(title: "错误", message: "请输入活动名称")
            return
        }

        let types = ["hiking", "camping", "climbing", "cycling"]
        let type = types[typeSegmentedControl.selectedSegmentIndex]

        let req = ActivityCreateReq(
            name: name,
            type: type,
            location: locationTextField.text,
            startDate: dateTextField.text,
            duration: durationTextField.text,
            description: descriptionTextView.text
        )

        APIService.shared.createActivity(req) { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success:
                    self?.onComplete?()
                    self?.dismiss(animated: true)
                case .failure(let error):
                    self?.showAlert(title: "错误", message: error.localizedDescription)
                }
            }
        }
    }

    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "确定", style: .default))
        present(alert, animated: true)
    }
}
