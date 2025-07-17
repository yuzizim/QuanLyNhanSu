// const Department = require('../models/departmentModel');

// // Create a new department
// exports.createDepartment = async (req, res) => {
//     try {
//         const { name, code, description, manager_id } = req.body;

//         // Create department in database
//         const department = await Department.create({
//             name,
//             code,
//             description,
//             manager_id
//         });

//         if (!department) {
//             return res.status(400).json({
//                 success: false,
//                 message: 'Failed to create department'
//             });
//         }

//         return res.status(201).json({
//             success: true,
//             message: 'Department successfully created',
//             data: department
//         });
//     } catch (error) {
//         console.error('Error in createDepartment:', error);
//         return res.status(500).json({
//             success: false,
//             message: 'Server error',
//             error: error.message
//         });
//     }
// };

// // Get all departments
// exports.getAllDepartments = async (req, res) => {
//     try {
//         const departments = await Department.findAll();
//         return res.status(200).json({
//             success: true,
//             data: departments
//         });
//     } catch (error) {
//         console.error('Error in getAllDepartments:', error);
//         return res.status(500).json({
//             success: false,
//             message: 'Server error',
//             error: error.message
//         });
//     }
// };

// // Get department by ID
// exports.getDepartmentById = async (req, res) => {
//     try {
//         const department = await Department.findById(req.params.id);
//         if (!department) {
//             return res.status(404).json({
//                 success: false,
//                 message: 'Department not found'
//             });
//         }
//         return res.status(200).json({
//             success: true,
//             data: department
//         });
//     } catch (error) {
//         console.error('Error in getDepartmentById:', error);
//         return res.status(500).json({
//             success: false,
//             message: 'Server error',
//             error: error.message
//         });
//     }
// };

// // Update a department
// exports.updateDepartment = async (req, res) => {
//     try {
//         const { name, code, description, manager_id } = req.body;
//         const department = await Department.update(req.params.id, {
//             name,
//             code,
//             description,
//             manager_id
//         });

//         if (!department) {
//             return res.status(404).json({
//                 success: false,
//                 message: 'Department not found or update failed'
//             });
//         }

//         return res.status(200).json({
//             success: true,
//             message: 'Department successfully updated',
//             data: department
//         });
//     } catch (error) {
//         console.error('Error in updateDepartment:', error);
//         return res.status(500).json({
//             success: false,
//             message: 'Server error',
//             error: error.message
//         });
//     }
// };

// // Delete a department
// exports.deleteDepartment = async (req, res) => {
//     try {
//         const deleted = await Department.delete(req.params.id);
//         if (!deleted) {
//             return res.status(404).json({
//                 success: false,
//                 message: 'Department not found or already deleted'
//             });
//         }

//         return res.status(200).json({
//             success: true,
//             message: 'Department successfully deleted'
//         });
//     } catch (error) {
//         console.error('Error in deleteDepartment:', error);
//         return res.status(500).json({
//             success: false,
//             message: 'Server error',
//             error: error.message
//         });
//     }
// };



// module.exports = exports;


const Department = require('../models/departmentModel');

// Validation result
const { validationResult } = require('express-validator');

// Get all departments with pagination and optional search
exports.getAllDepartments = async (req, res) => {
    try {
        const { search = '', page = 1, limit = 10 } = req.query;
        const data = await Department.getAllDepartments(search, parseInt(page), parseInt(limit));
        return res.json({ success: true, data });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
};

// Get department by ID
exports.getDepartmentById = async (req, res) => {
    try {
        const department = await Department.getDepartmentById(req.params.id);
        if (!department) {
            return res.status(404).json({ success: false, message: 'Department not found' });
        }
        return res.json({ success: true, data: department });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
};

// Create department
exports.createDepartment = async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ success: false, errors: errors.array() });
    }

    try {
        const departmentId = await Department.createDepartment(req.body);
        return res.status(201).json({ success: true, data: { id: departmentId } });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
};

// Update department
exports.updateDepartment = async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        return res.status(400).json({ success: false, errors: errors.array() });
    }

    try {
        const success = await Department.updateDepartment(req.params.id, req.body);
        if (!success) {
            return res.status(404).json({ success: false, message: 'Department not found' });
        }
        return res.json({ success: true });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
};

// Delete department
exports.deleteDepartment = async (req, res) => {
    try {
        const success = await Department.deleteDepartment(req.params.id);
        if (!success) {
            return res.status(404).json({ success: false, message: 'Department not found' });
        }
        return res.json({ success: true });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
};

// Get eligible managers
exports.getEligibleManagers = async (req, res) => {
    try {
        const managers = await Department.getEligibleManagers();
        return res.json({ success: true, data: managers });
    } catch (error) {
        return res.status(500).json({ success: false, message: error.message });
    }
};
