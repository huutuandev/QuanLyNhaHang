import "./CardItem.css"
function CardItem(props)
{
    const {title, style}=props;
    return(
        <>
        <div className="Card-item" style={style}>
            {title && <h4>{title}</h4>}
        </div>
        </>
    )
}
export default CardItem;