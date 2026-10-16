import "./App.css";
import { Routes, Route } from "react-router";

import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import CategoryManage from "./pages/CategoryManage";
import NewsManage from "./pages/NewsManage";
import UserManage from "./pages/UserManage";
import Settings from "./pages/Settings";
import NotFound from "./pages/NotFound";

function App() {
   return (
      <Routes>
         <Route path="/login" element={<Login />} />

         <Route path="/" element={<Layout />}>
            <Route index element={<Dashboard />} />
            <Route path="category" element={<CategoryManage />} />
            <Route path="news" element={<NewsManage />} />
            <Route path="users" element={<UserManage />} />
            <Route path="settings" element={<Settings />} />
          
            <Route path="*" element={<NotFound />} />
         </Route>
      </Routes>
   );
}

export default App;