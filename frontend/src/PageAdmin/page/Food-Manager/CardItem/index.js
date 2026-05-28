import { Card } from "antd";
import "./CardItem.scss"
function CardItem(props) {
    const { title, count, icon, color } = props;
    return (
        <>
            <Card className="card-item">
                <div className="card-item__content">
                    <div className="card-icon" style={{ backgroundColor: color }}>
                        {icon}
                    </div>
                    <div className="card-right">
                        <div className="card-title">
                            {title}
                        </div>
                        <div className="card-count">
                            {count}
                        </div>
                    </div>
                </div>
            </Card>
        </>
    )
}
export default CardItem;