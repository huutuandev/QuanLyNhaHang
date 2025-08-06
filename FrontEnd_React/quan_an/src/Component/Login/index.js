import Headers from "../Header";
import "./Login.scss";
import { useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

function Login() {
  const [isLogin, setIsLogin] = useState(true);

  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [gender, setGender] = useState("");
  const [dob, setDob] = useState("");
  const [email, setEmail] = useState("");
  const [registerPassword, setRegisterPassword] = useState("");

  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    if (!phoneNumber || !password) {
      setError("Vui lòng nhập đầy đủ số điện thoại và mật khẩu");
      setIsLoading(false);
      return;
    }

    if (!/^0\d{9}$/.test(phoneNumber)) {
      setError("Số điện thoại không hợp lệ");
      setIsLoading(false);
      return;
    }

    try {
      const response = await axios.post("/api/auth/login", {
        phoneNumber,
        password,
      });

      const { token } = response.data;
      localStorage.setItem("token", token);

      const meResponse = await axios.get("/api/users/profile", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const fullname = meResponse.data.fullname;
      localStorage.setItem("userName", fullname || "Người dùng");

      setSuccessMessage("Đăng nhập thành công!");
      const token1 = localStorage.getItem("token");
      const payload = JSON.parse(atob(token1.split('.')[1]));
      console.log(payload.roles);
      if (payload.roles.includes("ROLE_ADMIN")) {
        navigate("/admin");
      }
      else if (payload.roles.includes("ROLE_CUSTOMER")) {
        setTimeout(() => {
          navigate("/");
        }, 2000);
      }
    } catch (err) {
      const errorMessage =
        typeof err.response?.data === "string"
          ? err.response.data
          : "Đăng nhập thất bại. Vui lòng thử lại.";
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    if (!firstName || !lastName || !gender || !dob || !email || !registerPassword) {
      setError("Vui lòng điền đầy đủ thông tin");
      setIsLoading(false);
      return;
    }

    try {
      const response = await axios.post("/api/auth/register", {
        fullname: `${lastName} ${firstName}`,
        phoneNumber,
        email,
        password: registerPassword,
        retype_password: registerPassword,
        role_ids: [3],
      });

      toast.success("Đăng ký thành công!");
      setIsLogin(true);
    } catch (err) {
      const errorMessage =
        typeof err.response?.data === "string"
          ? err.response.data
          : "Đăng ký thất bại. Vui lòng thử lại.";
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Headers />
      <div className="login__container">
        <div className="login__box">
          <div className="switch-tabs">
            <span className={isLogin ? "active" : ""} onClick={() => setIsLogin(true)}>Đăng nhập</span>
            <span className={!isLogin ? "active" : ""} onClick={() => setIsLogin(false)}>Đăng ký</span>
          </div>

          <form onSubmit={isLogin ? handleLogin : handleRegister}>
            {!isLogin && (
              <>
                <input
                  type="text"
                  placeholder="Họ"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                />
                <input
                  type="text"
                  placeholder="Tên"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                />
                <div className="gender">
                  <label>
                    <input
                      type="radio"
                      name="gender"
                      value="Nữ"
                      onChange={() => setGender("Nữ")}
                    />
                    Nữ
                  </label>
                  <label>
                    <input
                      type="radio"
                      name="gender"
                      value="Nam"
                      onChange={() => setGender("Nam")}
                    />
                    Nam
                  </label>
                </div>
                <input
                  type="date"
                  value={dob}
                  onChange={(e) => setDob(e.target.value)}
                />
                <input
                  type="email"
                  placeholder="Email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </>
            )}

            <input
              type="text"
              placeholder="Số điện thoại"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
            />
            <input
              type="password"
              placeholder="Mật khẩu"
              value={isLogin ? password : registerPassword}
              onChange={(e) =>
                isLogin
                  ? setPassword(e.target.value)
                  : setRegisterPassword(e.target.value)
              }
            />

            {error && <p className="login__error">{error}</p>}
            {successMessage && <p className="login__success">{successMessage}</p>}

            {isLogin && (
              <div className="forgot-password">
                <span>Quên mật khẩu?</span>
              </div>
            )}

            <button type="submit" className="login__btn" disabled={isLoading}>
              {isLoading ? "Đang xử lý..." : isLogin ? "ĐĂNG NHẬP" : "ĐĂNG KÝ"}
            </button>
          </form>
        </div>
      </div>
    </>
  );
}

export default Login;
