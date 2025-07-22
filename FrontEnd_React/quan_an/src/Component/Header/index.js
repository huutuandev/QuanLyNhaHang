import "./Header.scss";
import { Link, NavLink } from "react-router-dom";
import { GrRestaurant } from "react-icons/gr";
import { FaUserAlt } from "react-icons/fa";
import { useState, useEffect, useRef } from "react";

function Header() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userName, setUserName] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const name = localStorage.getItem("userName");
    if (token) {
      setIsLoggedIn(true);
      setUserName(name || "Người dùng");
    }
  }, []);

  const toggleDropdown = () => {
    setShowDropdown(!showDropdown);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userName");
    setIsLoggedIn(false);
    window.location.href = "/login";
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="header">
      <div className="header__left">
        <GrRestaurant className="header__logo" />
        <span className="header__brand">THT</span>
      </div>

      <div className="header__right">
        <nav className="header__menu">
          <NavLink to="/">TRANG CHỦ</NavLink>
          <NavLink to="/Menu">THỰC ĐƠN</NavLink>
          <NavLink to="/Service">DỊCH VỤ</NavLink>
          <NavLink to="/News">TIN TỨC & MẸO HAY</NavLink>
          <NavLink to="/Orther">KHÁC</NavLink>
        </nav>

        <Link to="/Table">
          <button className="header__button">ĐẶT BÀN</button>
        </Link>

        {isLoggedIn ? (
          <div className="header__user-dropdown" ref={dropdownRef}>
            <FaUserAlt className="header__user-avatar" onClick={toggleDropdown} />
            {showDropdown && (
              <div className="header__dropdown-menu">
                <div className="header__dropdown-item">Xin chào, {userName}</div>
                <Link to="/Account">
                  <div className="header__dropdown-item">Tài khoản của tôi</div>
                </Link>
                <Link to="/HistoryBooking">
                  <div className="header__dropdown-item">Lịch sử đặt bàn</div>
                </Link>
                <div className="header__dropdown-item" onClick={handleLogout}>
                  Đăng xuất
                </div>
              </div>
            )}
          </div>
        ) : (
          <Link to="/Login">
            <button className="header__Login">ĐĂNG NHẬP</button>
          </Link>
        )}
      </div>
    </div>
  );
}

export default Header;
