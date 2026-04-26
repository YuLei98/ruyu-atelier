import UIKit

class ActivityCell: UITableViewCell {

    private let containerView = UIView()
    private let coverView = UIView()
    private let iconLabel = UILabel()
    private let badgeLabel = UILabel()
    private let favoriteButton = UIButton()
    private let titleLabel = UILabel()
    private let metaLabel = UILabel()
    private let statsStackView = UIStackView()

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
        containerView.layer.cornerRadius = 20
        containerView.layer.shadowColor = UIColor.black.cgColor
        containerView.layer.shadowOpacity = 0.06
        containerView.layer.shadowOffset = CGSize(width: 0, height: 2)
        containerView.layer.shadowRadius = 12
        containerView.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(containerView)

        coverView.layer.cornerRadius = 12
        coverView.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(coverView)

        iconLabel.font = .systemFont(ofSize: 64)
        iconLabel.textAlignment = .center
        iconLabel.translatesAutoresizingMaskIntoConstraints = false
        coverView.addSubview(iconLabel)

        badgeLabel.font = .systemFont(ofSize: 12, weight: .medium)
        badgeLabel.textColor = .white
        badgeLabel.backgroundColor = UIColor.black.withAlphaComponent(0.4)
        badgeLabel.layer.cornerRadius = 12
        badgeLabel.clipsToBounds = true
        badgeLabel.textAlignment = .center
        badgeLabel.translatesAutoresizingMaskIntoConstraints = false
        coverView.addSubview(badgeLabel)

        favoriteButton.setTitle("🤍", for: .normal)
        favoriteButton.backgroundColor = UIColor.white.withAlphaComponent(0.9)
        favoriteButton.layer.cornerRadius = 16
        favoriteButton.translatesAutoresizingMaskIntoConstraints = false
        coverView.addSubview(favoriteButton)

        titleLabel.font = .systemFont(ofSize: 17, weight: .semibold)
        titleLabel.textColor = UIColor(red: 30/255, green: 41/255, blue: 59/255, alpha: 1)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(titleLabel)

        metaLabel.font = .systemFont(ofSize: 13)
        metaLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        metaLabel.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(metaLabel)

        statsStackView.axis = .horizontal
        statsStackView.distribution = .fillEqually
        statsStackView.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(statsStackView)

        NSLayoutConstraint.activate([
            containerView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 8),
            containerView.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 16),
            containerView.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -16),
            containerView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -8),

            coverView.topAnchor.constraint(equalTo: containerView.topAnchor, constant: 12),
            coverView.leftAnchor.constraint(equalTo: containerView.leftAnchor, constant: 12),
            coverView.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -12),
            coverView.heightAnchor.constraint(equalToConstant: 160),

            iconLabel.centerXAnchor.constraint(equalTo: coverView.centerXAnchor),
            iconLabel.centerYAnchor.constraint(equalTo: coverView.centerYAnchor),

            badgeLabel.topAnchor.constraint(equalTo: coverView.topAnchor, constant: 12),
            badgeLabel.leftAnchor.constraint(equalTo: coverView.leftAnchor, constant: 12),
            badgeLabel.heightAnchor.constraint(equalToConstant: 24),
            badgeLabel.widthAnchor.constraint(greaterThanOrEqualToConstant: 50),

            favoriteButton.topAnchor.constraint(equalTo: coverView.topAnchor, constant: 12),
            favoriteButton.rightAnchor.constraint(equalTo: coverView.rightAnchor, constant: -12),
            favoriteButton.widthAnchor.constraint(equalToConstant: 32),
            favoriteButton.heightAnchor.constraint(equalToConstant: 32),

            titleLabel.topAnchor.constraint(equalTo: coverView.bottomAnchor, constant: 12),
            titleLabel.leftAnchor.constraint(equalTo: containerView.leftAnchor, constant: 16),
            titleLabel.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -16),

            metaLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 4),
            metaLabel.leftAnchor.constraint(equalTo: containerView.leftAnchor, constant: 16),
            metaLabel.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -16),

            statsStackView.topAnchor.constraint(equalTo: metaLabel.bottomAnchor, constant: 12),
            statsStackView.leftAnchor.constraint(equalTo: containerView.leftAnchor, constant: 16),
            statsStackView.rightAnchor.constraint(equalTo: containerView.rightAnchor, constant: -16),
            statsStackView.heightAnchor.constraint(equalToConstant: 40),
            statsStackView.bottomAnchor.constraint(equalTo: containerView.bottomAnchor, constant: -12)
        ])
    }

    func configure(with activity: Activity) {
        titleLabel.text = activity.name
        iconLabel.text = activity.coverIcon ?? "📍"

        let typeText: String
        switch activity.type {
        case "hiking": typeText = "徒步"
        case "camping": typeText = "露营"
        case "climbing": typeText = "登山"
        case "cycling": typeText = "骑行"
        default: typeText = activity.type ?? "活动"
        }
        badgeLabel.text = "  \(typeText)  "

        let color: UIColor
        switch activity.type {
        case "hiking": color = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        case "camping": color = UIColor(red: 245/255, green: 158/255, blue: 11/255, alpha: 1)
        case "climbing": color = UIColor(red: 99/255, green: 102/255, blue: 241/255, alpha: 1)
        case "cycling": color = UIColor(red: 236/255, green: 72/255, blue: 153/255, alpha: 1)
        default: color = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        }
        coverView.backgroundColor = color

        var metaParts: [String] = []
        if let location = activity.location {
            metaParts.append("📍 \(location)")
        }
        if let date = activity.startDate {
            metaParts.append("🗓 \(date)")
        }
        metaLabel.text = metaParts.joined(separator: "  ")

        statsStackView.arrangedSubviews.forEach { $0.removeFromSuperview() }

        if let distance = activity.totalDistance {
            let statView = createStatView(value: String(format: "%.1f", distance), label: "公里")
            statsStackView.addArrangedSubview(statView)
        }
        if let elevation = activity.totalElevation {
            let statView = createStatView(value: "\(elevation)", label: "米爬升")
            statsStackView.addArrangedSubview(statView)
        }
        if let duration = activity.duration {
            let statView = createStatView(value: duration, label: "时长")
            statsStackView.addArrangedSubview(statView)
        }

        favoriteButton.setTitle(activity.favorite == true ? "❤️" : "🤍", for: .normal)
    }

    private func createStatView(value: String, label: String) -> UIView {
        let view = UIView()
        view.backgroundColor = UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)
        view.layer.cornerRadius = 8

        let valueLabel = UILabel()
        valueLabel.text = value
        valueLabel.font = .systemFont(ofSize: 16, weight: .semibold)
        valueLabel.textColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        valueLabel.textAlignment = .center
        valueLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(valueLabel)

        let titleLabel = UILabel()
        titleLabel.text = label
        titleLabel.font = .systemFont(ofSize: 11)
        titleLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        titleLabel.textAlignment = .center
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(titleLabel)

        NSLayoutConstraint.activate([
            valueLabel.topAnchor.constraint(equalTo: view.topAnchor, constant: 6),
            valueLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            titleLabel.topAnchor.constraint(equalTo: valueLabel.bottomAnchor, constant: 2),
            titleLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor)
        ])

        return view
    }
}
