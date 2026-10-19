import { useState, useEffect } from "react";
import { Container, Table, Button, Form, InputGroup, Badge, Alert } from "react-bootstrap";
import { mockUsers } from "../shared/MockDatabase";
import FormModal from "../components/FormModal";
import ConfirmModal from "../components/ConfirmModal";

export default function UserManage() {
   const [users, setUsers] = useState(mockUsers);
   const [searchTerm, setSearchTerm] = useState("");
   const [message, setMessage] = useState("");
   const [error, setError] = useState("");
   
   const [showForm, setShowForm] = useState(false);
   const [showConfirm, setShowConfirm] = useState(false);
   const [currentEdit, setCurrentEdit] = useState(null);
   const [deleteId, setDeleteId] = useState(null);

   const [formData, setFormData] = useState({ username: "", password: "", name: "", role: 2, status: 1 });

   const token = localStorage.getItem("token");

   const fetchUsers = async () => {
      try {
         const res = await fetch("http://localhost:8080/api/users", {
            headers: token ? { "Authorization": `Bearer ${token}` } : {}
         });
         if (res.ok) {
            const data = await res.json();
            setUsers(data);
         }
      } catch (err) {
         // Nếu chưa bật backend, giữ nguyên dữ liệu mockUsers
      }
   };

   useEffect(() => {
      fetchUsers();
   }, []);

   const filteredData = users.filter(u => 
      u.username.toLowerCase().includes(searchTerm.toLowerCase()) || 
      u.name.toLowerCase().includes(searchTerm.toLowerCase())
   );

   const handleOpenForm = (item = null) => {
      setCurrentEdit(item);
      setError("");
      setMessage("");
      if (item) {
         setFormData({ username: item.username, password: "", name: item.name, role: item.role, status: item.status });
      } else {
         setFormData({ username: "", password: "", name: "", role: 2, status: 1 });
      }
      setShowForm(true);
   };

   const handleSubmitForm = async () => {
      setError("");
      setMessage("");
      if (currentEdit) {
         // Update
         try {
            const res = await fetch(`http://localhost:8080/api/users/${currentEdit.id}`, {
               method: "PUT",
               headers: {
                  "Content-Type": "application/json",
                  ...(token ? { "Authorization": `Bearer ${token}` } : {})
               },
               body: JSON.stringify(formData)
            });
            if (res.ok) {
               fetchUsers();
               setMessage("Cập nhật tài khoản thành công!");
               setShowForm(false);
               return;
            }
         } catch (e) {}

         // Fallback local update
         setUsers(users.map(u => u.id === currentEdit.id ? { ...u, ...formData } : u));
         setMessage("Cập nhật tài khoản thành công!");
      } else {
         // Create
         try {
            const res = await fetch("http://localhost:8080/api/users", {
               method: "POST",
               headers: {
                  "Content-Type": "application/json",
                  ...(token ? { "Authorization": `Bearer ${token}` } : {})
               },
               body: JSON.stringify(formData)
            });
            if (res.ok) {
               fetchUsers();
               setMessage("Tạo mới tài khoản thành công!");
               setShowForm(false);
               return;
            } else {
               const errData = await res.json();
               setError(errData.message || "Tạo tài khoản thất bại!");
               return;
            }
         } catch (e) {}

         // Fallback local create
         const newId = users.length > 0 ? Math.max(...users.map(u => u.id)) + 1 : 1;
         setUsers([...users, { id: newId, ...formData }]);
         setMessage("Tạo mới tài khoản thành công!");
      }
      setShowForm(false);
   };

   const handleConfirmDelete = async () => {
      setError("");
      setMessage("");
      try {
         const res = await fetch(`http://localhost:8080/api/users/${deleteId}`, {
            method: "DELETE",
            headers: token ? { "Authorization": `Bearer ${token}` } : {}
         });
         if (res.ok) {
            fetchUsers();
            setMessage("Đã xóa tài khoản thành công!");
            setShowConfirm(false);
            return;
         } else {
            const errData = await res.json();
            setError(errData.message || "Không thể xóa tài khoản!");
         }
      } catch (e) {}

      setUsers(users.filter(u => u.id !== deleteId));
      setMessage("Đã xóa tài khoản thành công!");
      setShowConfirm(false);
   };

   let currentUser = null;
   try {
      currentUser = JSON.parse(localStorage.getItem("user") || "null");
   } catch (e) {}

   // Nếu tài khoản là Staff (role !== 1), từ chối truy cập
   if (currentUser && currentUser.role !== 1) {
      return (
         <Container fluid className="py-5 text-center">
            <div className="p-5 rounded bg-dark border border-danger text-light shadow-lg d-inline-block" style={{ maxWidth: "600px" }}>
               <h3 className="text-danger fw-bold mb-3">⛔ Quyền truy cập bị từ chối</h3>
               <p className="fs-5">Chức năng <strong>Quản lý Tài khoản (User Management)</strong> chỉ dành riêng cho <strong>Quản trị viên (Admin)</strong>.</p>
               <p className="text-secondary mb-0">Tài khoản hiện tại của bạn là <Badge bg="info">Staff</Badge>, bạn không có quyền xem hoặc chỉnh sửa danh sách người dùng.</p>
            </div>
         </Container>
      );
   }

   return (
      <Container fluid>
         <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="text-white fw-bold">Quản lý Tài khoản</h2>
            <Button variant="light" className="glow-on-hover fw-bold" onClick={() => handleOpenForm()}>
               + Thêm Tài Khoản
            </Button>
         </div>

         {message && <Alert variant="success" dismissible onClose={() => setMessage("")}>{message}</Alert>}
         {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

         <InputGroup className="mb-4" style={{ width: "350px" }}>
            <Form.Control 
               placeholder="Tìm theo username hoặc tên..." 
               className="bg-dark text-white border-secondary"
               onChange={(e) => setSearchTerm(e.target.value)}
            />
         </InputGroup>

         <Table hover variant="dark" className="border-secondary align-middle shadow-sm">
            <thead>
               <tr>
                  <th>ID</th>
                  <th>Username</th>
                  <th>Họ và Tên</th>
                  <th>Phân quyền</th>
                  <th>Trạng thái</th>
                  <th className="text-end">Hành động</th>
               </tr>
            </thead>
            <tbody>
               {filteredData.map(u => (
                  <tr key={u.id}>
                     <td>{u.id}</td>
                     <td className="fw-bold">{u.username}</td>
                     <td>{u.name}</td>
                     <td>
                        <Badge bg={u.role === 1 ? "danger" : "info"}>
                           {u.role === 1 ? "Admin" : "Staff"}
                        </Badge>
                     </td>
                     <td>
                        <Badge bg={u.status === 1 ? "success" : "secondary"}>
                           {u.status === 1 ? "Active" : "Inactive"}
                        </Badge>
                     </td>
                     <td className="text-end">
                        <Button variant="outline-info" size="sm" className="me-2" onClick={() => handleOpenForm(u)}>Sửa</Button>
                        <Button variant="outline-danger" size="sm" onClick={() => { setDeleteId(u.id); setShowConfirm(true); }}>Xóa</Button>
                     </td>
                  </tr>
               ))}
               {filteredData.length === 0 && (
                  <tr><td colSpan="6" className="text-center text-secondary py-3">Không tìm thấy tài khoản</td></tr>
               )}
            </tbody>
         </Table>

         <FormModal 
            show={showForm} 
            onHide={() => setShowForm(false)} 
            onSubmit={handleSubmitForm}
            title={currentEdit ? "Cập nhật Tài khoản" : "Tạo Tài khoản mới"}
         >
            <Form>
               <Form.Group className="mb-3">
                  <Form.Label>Username</Form.Label>
                  <Form.Control type="text" className="bg-dark text-white border-secondary" value={formData.username} onChange={e => setFormData({...formData, username: e.target.value})} disabled={!!currentEdit} />
               </Form.Group>
               <Form.Group className="mb-3">
                  <Form.Label>Mật khẩu {currentEdit && "(Để trống nếu không muốn đổi)"}</Form.Label>
                  <Form.Control type="password" placeholder={currentEdit ? "Nhập mật khẩu mới..." : "Nhập mật khẩu (mặc định 123456)..."} className="bg-dark text-white border-secondary" value={formData.password} onChange={e => setFormData({...formData, password: e.target.value})} />
               </Form.Group>
               <Form.Group className="mb-3">
                  <Form.Label>Họ và Tên</Form.Label>
                  <Form.Control type="text" className="bg-dark text-white border-secondary" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
               </Form.Group>
               <Form.Group className="mb-3">
                  <Form.Label>Phân quyền</Form.Label>
                  <Form.Select className="bg-dark text-white border-secondary" value={formData.role} onChange={e => setFormData({...formData, role: Number(e.target.value)})}>
                     <option value={1}>Admin</option>
                     <option value={2}>Staff</option>
                  </Form.Select>
               </Form.Group>
               <Form.Group>
                  <Form.Label>Trạng thái</Form.Label>
                  <Form.Select className="bg-dark text-white border-secondary" value={formData.status} onChange={e => setFormData({...formData, status: Number(e.target.value)})}>
                     <option value={1}>Active</option>
                     <option value={0}>Inactive</option>
                  </Form.Select>
               </Form.Group>
            </Form>
         </FormModal>

         <ConfirmModal 
            show={showConfirm} 
            onHide={() => setShowConfirm(false)} 
            onConfirm={handleConfirmDelete}
            title="Xác nhận xóa tài khoản"
            message={`Bạn có chắc chắn muốn xóa tài khoản này không? Hành động này không thể hoàn tác.`}
         />
      </Container>
   );
}