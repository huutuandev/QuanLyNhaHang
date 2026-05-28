import { Card, Col, Row, DatePicker, Button } from 'antd';
import { useState } from 'react';
import axios from 'axios';
import { ShoppingCartOutlined, DollarOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import "./TKDoanhThu.scss"
function TKDoanhThu() {
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [totalRevenue, setTotalRevenue] = useState(0);
    const [totalOrders, setTotalOrders] = useState(0);

    const handleThongKe = async () => {
        if (!startDate || !endDate) return;
        try {
            const res = await axios.get('/api/dashboard/revenue', {
                params: {
                    startDate: startDate.format('YYYY-MM-DD'),
                    endDate: endDate.format('YYYY-MM-DD'),
                },
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            });
            setTotalRevenue(res.data.totalRevenue);
            setTotalOrders(res.data.totalOrders);
        } catch (error) {
            console.error('Lỗi khi lấy dữ liệu thống kê:', error);
        }
    };

    return (
        <>
            <div className="revenue-statistics">
                <h3>Thống Kê Doanh Thu</h3>
                <Row className="DateTK" gutter={[16, 16]} align="middle" style={{ marginBottom: '20px' }}>
                    <Col>
                        <DatePicker
                            placeholder="Ngày bắt đầu"
                            format="DD/MM/YYYY"
                            onChange={(date) => setStartDate(date)}
                            value={startDate}
                            className="DateStart"
                        />
                    </Col>
                    <Col>
                        <DatePicker
                            placeholder="Ngày kết thúc"
                            format="DD/MM/YYYY"
                            onChange={(date) => setEndDate(date)}
                            value={endDate}
                            className="DateEnd"
                        />
                    </Col>
                    <Col>
                        <Button className="btn-tk" type="primary" onClick={handleThongKe}>
                            Thống Kê
                        </Button>
                    </Col>
                </Row>

                <Row gutter={[16, 16]}>
                    <Col xs={24} sm={12}>
                        <Card>
                            <div style={{ display: 'flex', alignItems: 'center' }}>
                                <DollarOutlined style={{ fontSize: 30, color: '#52c41a', marginRight: 10 }} />
                                <div>
                                    <div><strong>Tổng Doanh Thu</strong></div>
                                    <div>{totalRevenue.toLocaleString()} đ</div>
                                </div>
                            </div>
                        </Card>
                    </Col>
                    <Col xs={24} sm={12}>
                        <Card>
                            <div style={{ display: 'flex', alignItems: 'center' }}>
                                <ShoppingCartOutlined style={{ fontSize: 30, color: '#1890ff', marginRight: 10 }} />
                                <div>
                                    <div><strong>Số Lượng Đơn Hàng</strong></div>
                                    <div>{totalOrders}</div>
                                </div>
                            </div>
                        </Card>
                    </Col>
                </Row>
            </div>
        </>
    )
}
export default TKDoanhThu;