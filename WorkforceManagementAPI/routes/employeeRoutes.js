// routes/employeeRoutes.js
const express = require('express');
const router = express.Router();
const employeeController = require('../controllers/employeeController');
const { verifyToken, checkRole } = require('../middleware/authMiddleware');
const { validateEmployeeData } = require('../middleware/employeeMiddleware');

router.post('/', verifyToken, checkRole('admin', 'hr'), validateEmployeeData, employeeController.createEmployee);
// router.get('/', verifyToken, checkRole('admin', 'hr'), employeeController.getAllEmployees);
router.get('/', verifyToken, checkRole('admin', 'hr'), employeeController.getAllEmployees);
router.get('/:id', verifyToken, checkRole('admin', 'hr'), employeeController.getEmployeeById);
router.put('/:id', verifyToken, checkRole('admin', 'hr'), validateEmployeeData, employeeController.updateEmployee);
router.delete('/:id', verifyToken, checkRole('admin', 'hr'), employeeController.deleteEmployee);


module.exports = router;