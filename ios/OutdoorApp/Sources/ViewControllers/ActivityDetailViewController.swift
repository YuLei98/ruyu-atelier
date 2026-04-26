import UIKit

class ActivityDetailViewController: UIViewController {

    private let activity: Activity
    private let scrollView = UIScrollView()
    private let contentView = UIView()
    private let headerView = UIView()
    private let iconLabel = UILabel()
    private let backButton = UIButton()
    private let titleLabel = UILabel()
    private let locationLabel = UILabel()
    private let badgesStackView = UIStackView()

    init(activity: Activity) {
        self.activity = activity
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }

    private func setupUI() {
        view.backgroundColor = UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)

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

        setupHeader()
        setupContent()
    }

    private func setupHeader() {
        headerView.layer.cornerRadius = 24
        headerView.layer.maskedCorners = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
        headerView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(headerView)

        iconLabel.text = activity.coverIcon ?? "📍"
        iconLabel.font = .systemFont(ofSize: 80)
        iconLabel.textAlignment = .center
        iconLabel.translatesAutoresizingMaskIntoConstraints = false
        headerView.addSubview(iconLabel)

        let color: UIColor
        switch activity.type {
        case "hiking": color = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        case "camping": color = UIColor(red: 245/255, green: 158/255, blue: 11/255, alpha: 1)
        case "climbing": color = UIColor(red: 99/255, green: 102/255, blue: 241/255, alpha: 1)
        case "cycling": color = UIColor(red: 236/255, green: 72/255, blue: 153/255, alpha: 1)
        default: color = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        }
        headerView.backgroundColor = color

        backButton.setImage(UIImage(systemName: "chevron.left"), for: .normal)
        backButton.tintColor = .white
        backButton.backgroundColor = UIColor.white.withAlphaComponent(0.9)
        backButton.layer.cornerRadius = 20
        backButton.translatesAutoresizingMaskIntoConstraints = false
        backButton.addTarget(self, action: #selector(goBack), for: .touchUpInside)
        contentView.addSubview(backButton)

        NSLayoutConstraint.activate([
            headerView.topAnchor.constraint(equalTo: contentView.topAnchor),
            headerView.leftAnchor.constraint(equalTo: contentView.leftAnchor),
            headerView.rightAnchor.constraint(equalTo: contentView.rightAnchor),
            headerView.heightAnchor.constraint(equalToConstant: 280),
            iconLabel.centerXAnchor.constraint(equalTo: headerView.centerXAnchor),
            iconLabel.centerYAnchor.constraint(equalTo: headerView.centerYAnchor),
            backButton.topAnchor.constraint(equalTo: contentView.safeAreaLayoutGuide.topAnchor, constant: 8),
            backButton.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 16),
            backButton.widthAnchor.constraint(equalToConstant: 40),
            backButton.heightAnchor.constraint(equalToConstant: 40)
        ])
    }

    private func setupContent() {
        titleLabel.text = activity.name
        titleLabel.font = .systemFont(ofSize: 24, weight: .bold)
        titleLabel.textColor = UIColor(red: 30/255, green: 41/255, blue: 59/255, alpha: 1)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(titleLabel)

        var locationText = "📍 "
        if let location = activity.location { locationText += location }
        if let date = activity.startDate { locationText += "  🗓 \(date)" }
        locationLabel.text = locationText
        locationLabel.font = .systemFont(ofSize: 14)
        locationLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        locationLabel.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(locationLabel)

        badgesStackView.axis = .horizontal
        badgesStackView.spacing = 8
        badgesStackView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(badgesStackView)

        let typeText: String
        switch activity.type {
        case "hiking": typeText = "🚶 徒步"
        case "camping": typeText = "⛺ 露营"
        case "climbing": typeText = "🧗 登山"
        case "cycling": typeText = "🚴 骑行"
        default: typeText = "📍 活动"
        }
        badgesStackView.addArrangedSubview(createBadge(text: typeText, color: UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)))

        NSLayoutConstraint.activate([
            titleLabel.topAnchor.constraint(equalTo: headerView.bottomAnchor, constant: 20),
            titleLabel.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 20),
            titleLabel.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -20),
            locationLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 8),
            locationLabel.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 20),
            locationLabel.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -20),
            badgesStackView.topAnchor.constraint(equalTo: locationLabel.bottomAnchor, constant: 16),
            badgesStackView.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 20),
            badgesStackView.heightAnchor.constraint(equalToConstant: 32)
        ])
    }

    private func createBadge(text: String, color: UIColor) -> UIView {
        let view = UIView()
        view.backgroundColor = color
        view.layer.cornerRadius = 16
        let label = UILabel()
        label.text = text
        label.font = .systemFont(ofSize: 12, weight: .medium)
        label.textColor = .white
        label.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(label)
        label.topAnchor.constraint(equalTo: view.topAnchor, constant: 6).isActive = true
        label.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -6).isActive = true
        label.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 12).isActive = true
        label.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -12).isActive = true
        return view
    }

    @objc private func goBack() {
        navigationController?.popViewController(animated: true)
    }
}
