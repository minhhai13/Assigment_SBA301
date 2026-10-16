import { Modal, Button } from "react-bootstrap";

export default function ConfirmModal({ show, onHide, onConfirm, title, message }) {
   return (
      <Modal show={show} onHide={onHide} centered backdrop="static" contentClassName="dark-modal-content">
         <Modal.Header closeButton>
            <Modal.Title className="text-danger fw-bold">
               {title || "Xác nhận xóa"}
            </Modal.Title>
         </Modal.Header>
         <Modal.Body>
            <p className="mb-0 fs-5">{message || "Bạn có chắc chắn muốn thực hiện hành động này?"}</p>
         </Modal.Body>
         <Modal.Footer>
            <Button variant="outline-light" className="glow-on-hover" onClick={onHide}>
               Hủy bỏ
            </Button>
            <Button variant="danger" className="glow-on-hover border-0" onClick={onConfirm}>
               Đồng ý xóa
            </Button>
         </Modal.Footer>
      </Modal>
   );
}