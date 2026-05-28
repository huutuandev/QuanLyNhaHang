
import { Route, Routes } from 'react-router-dom';
import './App.css';
import ProtectedRoute from './ProtectedRoute';
import Home from './Page/Home';
import Menu from './Page/Menu';
import Service from './Page/Service';
import Orther from './Page/orther';
import Error from './Page/Error';
import Layout from './Layout';
import News from './Page/News';
import Login from './Component/Login';
import Table from './Page/Table';
// import SelectTable from './Page/Table/SelectTable';
import SelectMenu from './Page/Table/SelectMenu';
import Pay from './Page/Table/Pay';
import DetailFoods from './Page/DetailFoods';
import ReservationHistory from './Page/HistoryBooking';
import PaymentSuccess from './Page/Table/PaymentSuccess';
import LayoutAdmin from './PageAdmin/LayOutAdmin';
import GridDashboard from './PageAdmin/page/Dashboard/Grid-Dashboard';
import GridPost from './PageAdmin/page/Post-Manage/Grid-Post';
import GridUsers from './PageAdmin/page/User-Manager/Grid-User';
import GridReservation from './PageAdmin/page/Reservation- Manage/Grid-Reservation';
import GridCustomer from './PageAdmin/page/Customer-Consulting/Grid-Customer';
import FoodCategory from './PageAdmin/page/Food-Manager/Food-Category';
import DeletedFood from './PageAdmin/page/Food-Manager/Deleted-Food';
import ListFood from './PageAdmin/page/Food-Manager/List-Food';
import AddFood from './PageAdmin/page/Food-Manager/AddFood';
import AddCatagory from './PageAdmin/page/Food-Manager/AddCatagory';
import EditPost from './PageAdmin/page/Post-Manage/Edit-Post';
import AddUser from './PageAdmin/page/User-Manager/AddUser';
import TableManagement from './PageAdmin/page/Table-Manager';
import AdminChat from './PageAdmin/page/Customer-Consulting/Grid-Customer';
// import AdminChat from './PageAdmin/page/AdminChat';
import Recovery from './PageAdmin/page/Food-Manager/Recovery';
function App() {
  const isLoggedIn = !!localStorage.getItem('token');
  return (
    <>
    
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="Menu" element={<Menu />} />
          <Route path="Service" element={<Service />} />
          <Route path="Orther" element={<Orther />} />
          <Route path="News" element={<News />} />
          <Route path="Table" element={<Table />} />
          <Route path="/DetailFoods" element={<DetailFoods />} />
        </Route>

        <Route
          path="/SelectMenu"
          element={
            <ProtectedRoute>
              <SelectMenu />
            </ProtectedRoute>
          }
        />
        <Route
          path="/pay"
          element={
            <ProtectedRoute>
              <Pay />
            </ProtectedRoute>
          }
        />
        <Route
          path="/ReservationHistory"
          element={
            <ProtectedRoute>
              <ReservationHistory />
            </ProtectedRoute>
          }
        />
        <Route
          path="/payment-Success"
          element={
            <ProtectedRoute>
              <PaymentSuccess />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin"
          element={
            <ProtectedRoute requiredRole="ROLE_ADMIN">
              <LayoutAdmin />
            </ProtectedRoute>
          }
        >
          <Route index element={<GridDashboard />} />
          <Route path="Food-Category" element={<FoodCategory />} />
          <Route path="Food-List" element={<ListFood />} />
          <Route path="Add-Food" element={<AddFood />} />
          <Route path="edit-category" element={<AddCatagory />} />
          <Route path="Delete-Food" element={<Recovery />} />
          <Route path="Post-Manager" element={<GridPost />} />
          <Route path="edit-post" element={<EditPost />} />
          <Route path="Account-Manager" element={<GridUsers />} />
          <Route path="Add-User" element={<AddUser />} />
          <Route path="Booking-Manager" element={<GridReservation />} />
          <Route path="Table-Manager" element={<TableManagement/>}/>
          <Route path="Customer-Support" element={<AdminChat />} />
        </Route>

        <Route path="/Login" element={<Login />} />
        <Route path="*" element={<Error />} />
      </Routes>
    </>
  );
}

export default App;
