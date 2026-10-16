import { useState } from "react";
import { Container, Table, Button, Form, InputGroup, Badge } from "react-bootstrap";
import { mockCategories } from "../shared/MockDatabase";
import FormModal from "../components/FormModal";
import ConfirmModal from "../components/ConfirmModal";

export default function CategoryManage() {
   const [categories, setCategories] = useState(mockCategories);
   const [searchTerm, setSearchTerm] = useState("");
   
   // State quản lý Modal
   const [showForm, setShowForm] = useState(false);
   const [showConfirm, setShowConfirm] = useState(false);
   const [currentEdit, setCurrentEdit] = useState(null); // null = Create, object = Update
   const [deleteId, setDeleteId] = useState(null);

   // State Form Data
   const [formData, setFormData] = useState({ name: "", description: "", status: 1 });

   // Tìm kiếm
   const filteredData = categories.filter(c => 
      c.name.toLowerCase().includes(searchTerm.toLowerCase())
   );

   // Xử lý Form Thêm/Sửa
   const handleOpenForm = (item = null) => {
      setCurrentEdit(item);
      if (item) {
         setFormData({ name: item.name, description: item.description, status: item.status });
      } else {
         setFormData({ name: "", description: "", status: 1 });
      }
      setShowForm(true);
   };

   const handleSubmitForm = () => {
      if (currentEdit) {
         // Cập nhật
         setCategories(categories.map(c => c.id === currentEdit.id ? { ...c, ...formData } : c));
      } else {
         // Thêm mới
         const newId = categories.length > 0 ? Math.max(...categories.map(c => c.id)) + 1 : 1;
         setCategories([...categories, { id: newId, ...formData }]);
      }
      setShowForm(false);
   };

   // Xử lý Xóa
   const handleConfirmDelete = () => {
      setCategories(categories.filter(c => c.id !== deleteId));
      setShowConfirm(false);
   };

   return (
      <Container fluid>
         <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="text-white fw-bold">Quản lý Danh mục</h2>
            <Button variant="light" className="glow-on-hover fw-bold" onClick={() => handleOpenForm()}>
               + Thêm Danh Mục
            </Button>
         </div>

         <InputGroup className="mb-4" style={{ width: "300px" }}>
            <Form.Control 
               placeholder="Tìm kiếm danh mục..." 
               className="bg-dark text-white border-secondary"
               onChange={(e) => setSearchTerm(e.target.value)}
            />
         </InputGroup>

         <Table hover variant="dark" className="border-secondary align-middle shadow-sm">
            <thead>
               <tr>
                  <th>ID</th>
                  <th>Tên danh mục</th>
                  <th>Mô tả</th>
                  <th>Trạng thái</th>
                  <th className="text-end">Hành động</th>
               </tr>
            </thead>
            <tbody>
               {filteredData.map(c => (
                  <tr key={c.id}>
                     <td>{c.id}</td>
                     <td className="fw-bold">{c.name}</td>
                     <td className="text-secondary">{c.description}</td>
                     <td>
                        <Badge bg={c.status === 1 ? "success" : "secondary"}>
                           {c.status === 1 ? "Active" : "Inactive"}
                        </Badge>
                     </td>
                     <td className="text-end">
                        <Button variant="outline-info" size="sm" className="me-2" onClick={() => handleOpenForm(c)}>Sửa</Button>
                        <Button variant="outline-danger" size="sm" onClick={() => { setDeleteId(c.id); setShowConfirm(true); }}>Xóa</Button>
                     </td>
                  </tr>
               ))}
               {filteredData.length === 0 && (
                  <tr><td colSpan="5" className="text-center text-secondary py-3">Không tìm thấy dữ liệu</td></tr>
               )}
            </tbody>
         </Table>

         {/* Sử dụng Component Modal dùng chung */}
         <FormModal 
            show={showForm} 
            onHide={() => setShowForm(false)} 
            onSubmit={handleSubmitForm}
            title={currentEdit ? "Cập nhật Danh mục" : "Thêm Danh mục mới"}
         >
            <Form>
               <Form.Group className="mb-3">
                  <Form.Label>Tên danh mục</Form.Label>
                  <Form.Control type="text" className="bg-dark text-white border-secondary" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
               </Form.Group>
               <Form.Group className="mb-3">
                  <Form.Label>Mô tả</Form.Label>
                  <Form.Control as="textarea" rows={3} className="bg-dark text-white border-secondary" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} />
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
            title="Xác nhận xóa danh mục"
            message="Danh mục này sẽ bị xóa vĩnh viễn. Bạn có chắc chắn không?"
         />
      </Container>
   );
}