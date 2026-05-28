import { useEffect, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import "./SelectTable.scss";

function SelectTable() {
  const [reservationData, setReservationData] = useState(null);
  const [tables, setTables] = useState([]);
  const [selectedTableId, setSelectedTableId] = useState(null);
  const navigate = useNavigate();
  const size=100;
  useEffect(() => {
    const reservation = localStorage.getItem("reservation");
    const token = localStorage.getItem("token");
    
    if (!token) {
      alert("Bạn cần đăng nhập để tiếp tục.");
      navigate("/login");
      return;
    }

    if (!reservation) {
      alert("Không tìm thấy thông tin đặt bàn.");
      navigate("/Table");
      return;
    }

    const resData = JSON.parse(reservation);
    setReservationData(resData);
    const dateOnly = resData.date.split("T")[0];
    const userTime = new Date(`${resData.date}T${resData.time}`);

    const fetchTables = async () => {
      try {
        const responseTables = await fetch("/api/tables?size=100", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!responseTables.ok) throw new Error("Không tải được bàn");

        const responseJson = await responseTables.json();
        const tablesData = responseJson.content || [];


        const responseUnavailable = await fetch(
          `/api/reservations/unavailable-tables?date=${dateOnly}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        if (!responseUnavailable.ok)
          throw new Error("Không tải được bàn đã đặt");

        const unavailableList = await responseUnavailable.json();

        const formattedTables = tablesData.map((item) => {
          const matchedReservation = unavailableList.find(
            (res) => res.tableId === item.id
          );

          let status = "available";
          let startTime = null;
          let endTime = null;

          if (matchedReservation) {
            startTime = matchedReservation.startTime;
            endTime = matchedReservation.endTime;

            const start = new Date(`${dateOnly}T${startTime}`);
            const end = new Date(`${dateOnly}T${endTime}`);
            const diffHours = (start - userTime) / (1000 * 60 * 60);

            if (userTime >= start && userTime <= end) {
              status = "unavailable";
            } else if (diffHours > 0 && diffHours < 4) {
              status = "tooSoon";
            }
          }

          return {
            id: item.id,
            name: `Bàn ${item.tableNumber || item.id}`,
            status,
            startTime,
            endTime,
          };
        });

        setTables(formattedTables);
      } catch (err) {
        console.error("Lỗi:", err);
        alert("Không thể tải dữ liệu. Vui lòng đăng nhập lại.");
        navigate("/login");
      }
    };

    fetchTables();
  }, [navigate]);


  if (!reservationData)
    return <p>Đang tải thông tin đặt bàn...</p>;

  const formattedDate = new Date(reservationData.date).toLocaleString(
    "vi-VN",
    {
      dateStyle: "short",
      timeStyle: "short",
    }
  );

  const handleSelectTable = (table) => {
    if (table.status !== "available") return;

    setSelectedTableId(table.id);

    const updatedData = {
      ...reservationData,
      selectedTable: table,
      tableId: table.id,
    };

    localStorage.setItem("reservation", JSON.stringify(updatedData));
    navigate("/SelectMenu");
  };

  return (
    <div className="Main_SelectTable">
      <section className="content1-SelectTable">
        <div className="content1-SelectTable__text">
          <h1>Đặt bàn online</h1>
          <NavLink to="/">Trang Chủ /</NavLink>
          <NavLink to="/Table"> Đặt Bàn /</NavLink>
          <NavLink to="/SelectTable"> Chọn Bàn</NavLink>
        </div>
      </section>

      <section className="content2-SelectTable">
        <div className="information-Customer">
          <h2>Thông tin đặt bàn</h2>
          <p><strong>Họ tên:</strong> {reservationData.name}</p>
          <p><strong>Email:</strong> {reservationData.email}</p>
          <p><strong>Số điện thoại:</strong> {reservationData.phone}</p>
          <p><strong>Thời gian đặt bàn:</strong> {formattedDate}</p>
          <p><strong>Số người:</strong> {reservationData.numberOfPeople}</p>
          <p><strong>Ghi chú:</strong> {reservationData.note || "Không có"}</p>
        </div>

        <div className="choose-table">
          <h2>Chọn bàn</h2>
          <div className="table-list">
            {tables.map((table) => (
              <div
                key={table.id}
                className={`table-item ${table.status} ${selectedTableId === table.id ? "selected" : ""
                  }`}
                onClick={() => handleSelectTable(table)}
              >
                {table.name}
                {table.status === "unavailable" && table.startTime && table.endTime && (
                  <div className="reserved-time">
                    <small>Đã đặt từ {table.startTime} đến {table.endTime}</small>
                  </div>
                )}
                {table.status === "tooSoon" && (
                  <div className="reserved-time">
                    <small>Cần đặt trước tối thiểu 4 tiếng</small>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}

export default SelectTable;
