import { NavLink } from "react-router-dom";
import "./HistoryBooking.scss"

function HistoryBooking(){
    return(
        <>
        <div className="HistoryBooking">
                <section className="content1">
                    <div className="content1__text">
                        <h1 className="">lịch Sử Đặt Bàn</h1>
                        <NavLink to="/">Trang Chủ /</NavLink>
                        <NavLink to="/HistoryBooking"> Lịch Sử</NavLink>
                    </div>
                </section>
            </div>
        </>
    )
}
export default HistoryBooking;