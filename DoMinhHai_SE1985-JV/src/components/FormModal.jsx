import { Modal, Button } from "react-bootstrap";

export default function FormModal({ show, onHide, title, onSubmit, children, submitText = "Lưu lại" }) {
   return (
      <Modal show={show} onHide={onHide} centered backdrop="static" size="lg" contentClassName="dark-modal-content">
         <Modal.Header closeButton>
            <Modal.Title className="fw-bold">
               {title}
            </Modal.Title>
         </Modal.Header>
         <Modal.Body>
            {children}
         </Modal.Body>
         <Modal.Footer>
            <Button variant="outline-light" className="glow-on-hover" onClick={onHide}>
               Đóng
            </Button>
            <Button variant="light" className="glow-on-hover fw-bold" onClick={onSubmit}>
               {submitText}
            </Button>
         </Modal.Footer>
      </Modal>
   );
}