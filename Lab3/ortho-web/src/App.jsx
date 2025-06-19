import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import DoctorPage from "./pages/DoctorPage";
import AdminPage from "./pages/AdminPage";
import { getRole } from "./auth";

export default function App() {
  const role = getRole();

  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/doctor" element={role === "doctor" ? <DoctorPage /> : <Navigate to="/" />} />
      <Route path="/admin" element={role === "admin" ? <AdminPage /> : <Navigate to="/" />} />
    </Routes>
  );
}
