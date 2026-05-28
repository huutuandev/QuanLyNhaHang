import "./Menu.scss"
import { NavLink } from "react-router-dom";
import MenuList from "./MenuList";
function Menu() {
    return (
        <>
            <div className="Menu">
                <section className="content1">
                    <div className="content1__text">
                        <h1 className="">THỰC ĐƠN</h1>
                        <NavLink to="/">Trang Chủ /</NavLink>
                        <NavLink to="/Menu"> Thực Đơn</NavLink>
                    </div>
                </section>
                <section className="content2">
                    <MenuList/>
                </section>

            </div>
        </>
    )
}
export default Menu;