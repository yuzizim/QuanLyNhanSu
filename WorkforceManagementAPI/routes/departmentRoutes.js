// const express = require('express');
// const router = express.Router();
// const departmentController = require('../controllers/departmentController');
// const { verifyToken, checkRole } = require('../middleware/authMiddleware');
// const { validateDepartmentData } = require('../middleware/departmentMiddleware');

// router.post(
//     '/',
//     verifyToken,
//     checkRole('admin', 'hr', 'dep_manager'),
//     validateDepartmentData,
//     departmentController.createDepartment
// );
// router.get(
//     '/',
//     verifyToken,
//     checkRole('admin', 'hr', 'dep_manager'),
//     departmentController.getAllDepartments
// );
// router.get(
//     '/:id',
//     verifyToken,
//     checkRole('admin', 'hr', 'dep_manager'),
//     departmentController.getDepartmentById
// );
// router.put(
//     '/:id',
//     verifyToken,
//     checkRole('admin', 'hr', 'dep_manager'),
//     validateDepartmentData,
//     departmentController.updateDepartment
// );
// router.delete(
//     '/:id',
//     verifyToken,
//     checkRole('admin', 'hr', 'dep_manager'),
//     departmentController.deleteDepartment
// );

// module.exports = router;


const express = require('express');
const router = express.Router();
const departmentController = require('../controllers/departmentController');
const { authenticate, checkRole } = require('../middleware/authMiddleware');
const { body } = require('express-validator');

// Validation middleware
const departmentValidation = [
    body('name').notEmpty().withMessage('Name is required').isLength({ max: 255 }).withMessage('Name too long'),
    body('code').notEmpty().withMessage('Code is required').isLength({ max: 50 }).withMessage('Code too long'),
    body('description').optional().isLength({ max: 1000 }).withMessage('Description too long'),
    body('manager_id').optional().isInt().withMessage('Invalid manager ID')
];

// Routes
router.get('/', authenticate, departmentController.getAllDepartments);
router.get('/:id', authenticate, departmentController.getDepartmentById);
router.post('/', authenticate, checkRole('admin'), departmentValidation, departmentController.createDepartment);
router.put('/:id', authenticate, checkRole('admin'), departmentValidation, departmentController.updateDepartment);
router.delete('/:id', authenticate, checkRole('admin'), departmentController.deleteDepartment);
router.get('/managers/eligible', authenticate, departmentController.getEligibleManagers);

module.exports = router;
