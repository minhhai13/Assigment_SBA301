import { useState } from "react";
import { Container, Table, Button, Form, InputGroup, Badge } from "react-bootstrap";
import { mockNews, mockCategories, mockUsers } from "../shared/MockDatabase";
import FormModal from "../components/FormModal";
import ConfirmModal from "../components/ConfirmModal";

export default function NewsManage() {
   const [newsList, setNewsList] = useState(mockNews);
   const [searchTerm, setSearchTerm] = useState("");
   const [categoryFilter, setCategoryFilter] = useState("");
   
   const [showForm, setShowForm] = useState(false);
   const [showConfirm, setShowConfirm] = useState(false);
   const [currentEdit, setCurrentEdit] = useState(null);
   const [deleteId, setDeleteId] = useState(null);

   const [formData, setFormData] = useState({ title: "", categoryId: 1, authorId: 1, tags: "", status: 1 });

   const filteredData = newsList.filter(n => 
      n.title.toLowerCase().includes(searchTerm.toLowerCase()) &&
      (!categoryFilter || n.categoryId === Number(categoryFilter))
   );

   const handleOpenForm = (item = null) => {
      setCurrentEdit(item);
      if (item) {
         setFormData({ title: item.title, categoryId: item.categoryId, authorId: item.authorId ?? 1, tags: item.tags, status: item.status });
      } else {
         setFormData({ title: "", categoryId: mockCategories.find(category => category.status === 1)?.id || 1, authorId: mockUsers.find(user => user.status === 1)?.id || 1, tags: "", status: 1 });
      }
      setShowForm(true);
   };

   const handleSubmitForm = () => {
      if (currentEdit) {
         setNewsList(newsList.map(n => n.id === currentEdit.id ? { ...n, ...formData } : n));
      } else {
         const newId = newsList.length > 0 ? Math.max(...newsList.map(n => n.id)) + 1 : 1;
         setNewsList([...newsList, { id: newId, ...formData }]);
      }
      setShowForm(false);
   };

   const handleConfirmDelete = () => {
      setNewsList(newsList.filter(n => n.id !== deleteId));
      setShowConfirm(false);
   };

   const getCategoryName = (id) => {
      const cat = mockCategories.find(c => c.id === Number(id));
      return cat ? cat.name : "Không xác định";
   };

   const getAuthorName = (id) => {
      const author = mockUsers.find(user => user.id === Number(id));
      return author ? author.name : "Không xác định";
   };

   return (
      <Container fluid>
         <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="text-white fw-bold">Quản lý Tin tức</h2>
            <Button variant="light" className="glow-on-hover fw-bold" onClick={() => handleOpenForm()}>
               + Thêm Bài Viết
            </Button>
         </div>

         <div className="d-flex flex-wrap gap-2 mb-4">
            <InputGroup style={{ width: "350px" }}>
               <Form.Control 
                  placeholder="Tìm kiếm bài viết..." 
                  className="bg-dark text-white border-secondary glow-on-hover"
                  onChange={(e) => setSearchTerm(e.target.value)}
               />
            </InputGroup>
            <Form.Select
               aria-label="Lọc theo danh mục"
               className="bg-dark text-white border-secondary"
               style={{ width: "220px" }}
               value={categoryFilter}
               onChange={(e) => setCategoryFilter(e.target.value)}
            >
               <option value="">Tất cả danh mục</option>
               {mockCategories.map(category => (
                  <option key={category.id} value={category.id}>{category.name}</option>
               ))}
            </Form.Select>
         </div>

         <Table responsive hover variant="dark" className="border-secondary align-middle shadow-sm">
            <thead>
               <tr>
                  <th>ID</th>
                  <th className="text-center align-middle">Tiêu đề</th>
                  <th>Người đăng</th>
                  <th>Danh mục</th>
                  <th>Tags</th>
                  <th>Trạng thái</th>
                  <th className="text-end">Hành động</th>
               </tr>
            </thead>
            <tbody>
               {filteredData.map(n => (
                  <tr key={n.id}>
                     <td>{n.id}</td>
                     <td className="fw-bold text-center align-middle">{n.title}</td>
                     <td>{getAuthorName(n.authorId)}</td>
                     <td className="text-info">{getCategoryName(n.categoryId)}</td>
                     <td>{n.tags.split(',').map((tag, idx) => <Badge bg="secondary" className="me-1" key={idx}>{tag.trim()}</Badge>)}</td>
                     <td>
                        <Badge bg={n.status === 1 ? "success" : "secondary"}>
                           {n.status === 1 ? "Show" : "Hide"}
                        </Badge>
                     </td>
                     <td>
                        <div className="d-flex justify-content-end flex-nowrap gap-2">
                           <Button variant="outline-info" size="sm" onClick={() => handleOpenForm(n)}>Sửa</Button>
                           <Button variant="outline-danger" size="sm" onClick={() => { setDeleteId(n.id); setShowConfirm(true); }}>Xóa</Button>
                        </div>
                     </td>
                  </tr>
               ))}
               {filteredData.length === 0 && (
                  <tr><td colSpan="7" className="text-center text-secondary py-3">Không tìm thấy bài viết</td></tr>
               )}
            </tbody>
         </Table>

         <FormModal 
            show={showForm} 
            onHide={() => setShowForm(false)} 
            onSubmit={handleSubmitForm}
            title={currentEdit ? "Cập nhật Bài viết" : "Thêm Bài viết mới"}
         >
            <Form>
               <Form.Group className="mb-3">
                  <Form.Label>Tiêu đề</Form.Label>
                  <Form.Control type="text" className="bg-dark text-white border-secondary" value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} />
               </Form.Group>
               <Form.Group className="mb-3">
                  <Form.Label>Danh mục</Form.Label>
                  <Form.Select className="bg-dark text-white border-secondary" value={formData.categoryId} onChange={e => setFormData({...formData, categoryId: Number(e.target.value)})}>
                     {mockCategories.filter(c => c.status === 1).map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                     ))}
                  </Form.Select>
               </Form.Group>
               <Form.Group className="mb-3">
                  <Form.Label>Người đăng</Form.Label>
                  <Form.Select className="bg-dark text-white border-secondary" value={formData.authorId} onChange={e => setFormData({ ...formData, authorId: Number(e.target.value) })}>
                     {mockUsers.filter(user => user.status === 1).map(user => (
                        <option key={user.id} value={user.id}>{user.name}</option>
                     ))}
                  </Form.Select>
               </Form.Group>
               <Form.Group className="mb-3">
                  <Form.Label>Tags (ngăn cách bằng dấu phẩy)</Form.Label>
                  <Form.Control type="text" className="bg-dark text-white border-secondary" placeholder="vd: tech, new" value={formData.tags} onChange={e => setFormData({...formData, tags: e.target.value})} />
               </Form.Group>
               <Form.Group>
                  <Form.Label>Trạng thái</Form.Label>
                  <Form.Select className="bg-dark text-white border-secondary" value={formData.status} onChange={e => setFormData({...formData, status: Number(e.target.value)})}>
                     <option value={1}>Show</option>
                     <option value={0}>Hide</option>
                  </Form.Select>
               </Form.Group>
            </Form>
         </FormModal>

         <ConfirmModal 
            show={showConfirm} 
            onHide={() => setShowConfirm(false)} 
            onConfirm={handleConfirmDelete}
            title="Xác nhận xóa bài viết"
            message="Bài viết này sẽ bị xóa vĩnh viễn khỏi hệ thống. Bạn có chắc chắn không?"
         />
      </Container>
   );
}