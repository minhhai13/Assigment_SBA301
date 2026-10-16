import { useState } from "react";
import { Container, Table, Button, Form, InputGroup, Badge } from "react-bootstrap";
import { mockUsers } from "../shared/MockDatabase";
import FormModal from "../components/FormModal";
import ConfirmModal from "../components/ConfirmModal";

export default function UserManage() {
   const [users, setUsers] = useState(mockUsers);
   const [searchTerm, setSearchTerm] = useState("");
   
   const [showForm, setShowForm] = useState(false);
   const [showConfirm, setShowConfirm] = useState(false);
   const [currentEdit, setCurrentEdit] = useState(null);
   const [deleteId, setDeleteId] = useState(null);

   const [formData, setFormData] = useState({ username: "", name: "", role: 2, status: 1 });

   const filteredData = users.filter(u => 
      u.username.toLowerCase().includes(searchTerm.toLowerCase()) || 
      u.name.toLowerCase().includes(searchTerm.toLowerCase())
   );

   const handleOpenForm = (item = null) => {
      setCurrentEdit(item);
      if (item) {
         setFormData({ username: item.username, name: item.name, role: item.role, status: item.status });
      } else {
         setFormData({ username: "", name: "", role: 2, status: 1 });
      }
      setShowForm(true);
   };

   const handleSubmitForm = () => {
      if (currentEdit) {
         setUsers(users.map(u => u.id === currentEdit.id ? { ...u, ...formData } : u));
      } else {
         const newId = users.length > 0 ? Math.max(...users.map(u => u.id)) + 1 : 1;
         setUsers([...users, { id: newId, ...formData }]);
      }
      setShowForm(false);
   };

   const handleConfirmDelete = () => {
      setUsers(users.filter(u => u.id !== deleteId));
      setShowConfirm(false);
   };

   return (
      <Container fluid>
         <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="text-white fw-bold">Quản lý Tài khoản</h2>
            <Button variant="light" className="glow-on-hover fw-bold" onClick={() => handleOpenForm()}>
               + Thêm Tài Khoản
            </Button>
         </div>

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