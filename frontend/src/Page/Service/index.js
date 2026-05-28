import { NavLink } from "react-router-dom";
import { LuChefHat } from "react-icons/lu";
import { TbToolsKitchen2 } from "react-icons/tb";
import { FaChair } from "react-icons/fa";
import { SlEarphonesAlt } from "react-icons/sl";
import "./Service.scss"
function Service() {
    return (
        <>
            <div className="Service">
                <section className="content1-Service">
                    <div className="content1-Service__text">
                        <h1 className="">THỰC ĐƠN</h1>
                        <NavLink to="/">Trang Chủ /</NavLink>
                        <NavLink to="/Service"> Dịch Vụ</NavLink>
                    </div>
                </section>
                <section className="content2-Service">
                    <div className="content2-Service__before">
                        <div className="text">
                            <span>_______________ </span>
                            <p> Dịch vụ của chúng tôi </p>
                             <span> _______________</span>
                        </div>
                        <h2>Khám phá dịch vụ của chúng tôi</h2>
                    </div>
                    <div className="content2-Service__after">
                        <div className="box">
                        <LuChefHat className="icon" />
                        <h3 className="title">
                            Đầu bếp nhiều năm kinh nghiệm
                        </h3>
                        <span className="text">
                            Đầu bếp chung tôi với 5 năm king nghiệm sẽ luôn mang đến cho quý khách những món ăn hảo hạng
                        </span>
                    </div>
                    <div className="box">
                        <TbToolsKitchen2 className="icon" />
                        <h3 className="title">
                            Nguyên liệu tươi ngon nhất
                        </h3>
                        <span className="text">
                            Mỗi món ăn tại nhà hàng đều được chế biến từ những nguyên liệu tươi ngon nhất
                        </span>
                    </div>
                    <div className="box">
                        <FaChair className="icon" />
                        <h3 className="title">
                            Đặt bàn dễ dàng nhanh chóng
                        </h3>
                        <span className="text">
                            Đặt bàn dễ dàng chỉ với vài cú click. Món ăn sẽ nhanh chóng được phục vụ khi khách hàng đến nơi
                        </span>
                    </div>
                    <div className="box">
                        <SlEarphonesAlt className="icon" />
                        <h3 className="title">
                            Phục vụ tận tình xuyên suốt 24/7
                        </h3>
                        <span className="text">
                            Chúng tôi vẫn luôn sẳn sàng phục vụ quý khách 24/7.Liên hệ với chúng tôi ngay để được tư vấn dịch vụ nhà hàng và đặt bàn.
                        </span>
                    </div>
                    <div className="box">
                        <LuChefHat className="icon" />
                        <h3 className="title">
                            Đầu bếp nhiều năm kinh nghiệm
                        </h3>
                        <span className="text">
                            Đầu bếp chung tôi với 5 năm king nghiệm sẽ luôn mang đến cho quý khách những món ăn hảo hạng
                        </span>
                    </div>
                    <div className="box">
                        <TbToolsKitchen2 className="icon" />
                        <h3 className="title">
                            Nguyên liệu tươi ngon nhất
                        </h3>
                        <span className="text">
                            Mỗi món ăn tại nhà hàng đều được chế biến từ những nguyên liệu tươi ngon nhất
                        </span>
                    </div>
                    <div className="box">
                        <FaChair className="icon" />
                        <h3 className="title">
                            Đặt bàn dễ dàng nhanh chóng
                        </h3>
                        <span className="text">
                            Đặt bàn dễ dàng chỉ với vài cú click. Món ăn sẽ nhanh chóng được phục vụ khi khách hàng đến nơi
                        </span>
                    </div>
                    <div className="box">
                        <SlEarphonesAlt className="icon" />
                        <h3 className="title">
                            Phục vụ tận tình xuyên suốt 24/7
                        </h3>
                        <span className="text">
                            Chúng tôi vẫn luôn sẳn sàng phục vụ quý khách 24/7.Liên hệ với chúng tôi ngay để được tư vấn dịch vụ nhà hàng và đặt bàn.
                        </span>
                    </div>
                    </div>
                    
                </section>
            </div>
        </>
    )
}
export default Service;