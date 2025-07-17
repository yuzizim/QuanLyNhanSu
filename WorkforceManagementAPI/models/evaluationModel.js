// evaluationModel.js

// const db = require('../config/db');
// const moment = require('moment-timezone');

// class Evaluation {
//     static async create(evaluationData) {
//         try {
//             const {
//                 employee_id, evaluator_id, evaluation_date, performance_score,
//                 kpi_score, overall_score, strengths, improvement_areas, comments, status
//             } = evaluationData;

//             const query = `
//                 INSERT INTO evaluations (
//                     employee_id, evaluator_id, evaluation_date, performance_score,
//                     kpi_score, overall_score, strengths, improvement_areas, comments, status
//                 ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
//             `;
//             const values = [
//                 employee_id, evaluator_id, moment.utc(evaluation_date).format('YYYY-MM-DD'),
//                 performance_score, kpi_score, overall_score, strengths || null,
//                 improvement_areas || null, comments || null, status || 'draft'
//             ];
//             console.log('Creating evaluation with values:', values);
//             const [result] = await db.execute(query, values);
//             return result.insertId ? await this.findById(result.insertId) : null;
//         } catch (error) {
//             throw new Error(`Không thể tạo đánh giá: ${error.message}`);
//         }
//     }

//     static async findById(id) {
//         try {
//             const [rows] = await db.execute(`
//                 SELECT e.*, 
//                        CONCAT(emp.first_name, ' ', emp.last_name) as employee_name,
//                        CONCAT(eva.first_name, ' ', eva.last_name) as evaluator_name,
//                        d.name as department_name
//                 FROM evaluations e
//                 LEFT JOIN employees emp ON e.employee_id = emp.id
//                 LEFT JOIN employees eva ON e.evaluator_id = eva.id
//                 LEFT JOIN departments d ON emp.department_id = d.id
//                 WHERE e.id = ?
//             `, [id]);
//             return rows[0] || null;
//         } catch (error) {
//             throw new Error(`Không thể tìm đánh giá: ${error.message}`);
//         }
//     }

//     static async getAll({ employee_id, evaluator_id, start_date, end_date, status, department_id, page = 1, limit = 10 }) {
//         try {
//             const offset = (page - 1) * limit;
//             let query = `
//                 SELECT e.*, 
//                        CONCAT(emp.first_name, ' ', emp.last_name) as employee_name,
//                        CONCAT(eva.first_name, ' ', eva.last_name) as evaluator_name,
//                        d.name as department_name
//                 FROM evaluations e
//                 LEFT JOIN employees emp ON e.employee_id = emp.id
//                 LEFT JOIN employees eva ON e.evaluator_id = eva.id
//                 LEFT JOIN departments d ON emp.department_id = d.id
//                 WHERE 1=1
//             `;
//             const values = [];

//             if (employee_id) {
//                 query += ' AND e.employee_id = ?';
//                 values.push(employee_id);
//             }
//             if (evaluator_id) {
//                 query += ' AND e.evaluator_id = ?';
//                 values.push(evaluator_id);
//             }
//             if (start_date) {
//                 query += ' AND e.evaluation_date >= ?';
//                 values.push(moment.utc(start_date).format('YYYY-MM-DD'));
//             }
//             if (end_date) {
//                 query += ' AND e.evaluation_date <= ?';
//                 values.push(moment.utc(end_date).format('YYYY-MM-DD'));
//             }
//             if (status) {
//                 query += ' AND e.status = ?';
//                 values.push(status);
//             }
//             if (department_id) {
//                 query += ' AND emp.department_id = ?';
//                 values.push(department_id);
//             }

//             query += ' ORDER BY e.evaluation_date DESC LIMIT ? OFFSET ?';
//             values.push(limit, offset);

//             const [rows] = await db.execute(query, values);

//             const [countResult] = await db.execute(`
//                 SELECT COUNT(*) as total
//                 FROM evaluations e
//                 LEFT JOIN employees emp ON e.employee_id = emp.id
//                 WHERE 1=1
//                 ${employee_id ? 'AND e.employee_id = ?' : ''}
//                 ${evaluator_id ? 'AND e.evaluator_id = ?' : ''}
//                 ${start_date ? 'AND e.evaluation_date >= ?' : ''}
//                 ${end_date ? 'AND e.evaluation_date <= ?' : ''}
//                 ${status ? 'AND e.status = ?' : ''}
//                 ${department_id ? 'AND emp.department_id = ?' : ''}
//             `, values.slice(0, -2));

//             return {
//                 records: rows,
//                 total: countResult[0].total,
//                 page,
//                 limit
//             };
//         } catch (error) {
//             throw new Error(`Không thể lấy danh sách đánh giá: ${error.message}`);
//         }
//     }

//     static async update(id, evaluationData) {
//         try {
//             const {
//                 evaluation_date, performance_score, kpi_score, overall_score,
//                 strengths, improvement_areas, comments, status
//             } = evaluationData;

//             const fields = [];
//             const values = [];

//             if (evaluation_date) {
//                 fields.push('evaluation_date = ?');
//                 values.push(moment.utc(evaluation_date).format('YYYY-MM-DD'));
//             }
//             if (performance_score !== undefined) {
//                 fields.push('performance_score = ?');
//                 values.push(performance_score);
//             }
//             if (kpi_score !== undefined) {
//                 fields.push('kpi_score = ?');
//                 values.push(kpi_score);
//             }
//             if (overall_score !== undefined) {
//                 fields.push('overall_score = ?');
//                 values.push(overall_score);
//             }
//             if (strengths !== undefined) {
//                 fields.push('strengths = ?');
//                 values.push(strengths || null);
//             }
//             if (improvement_areas !== undefined) {
//                 fields.push('improvement_areas = ?');
//                 values.push(improvement_areas || null);
//             }
//             if (comments !== undefined) {
//                 fields.push('comments = ?');
//                 values.push(comments || null);
//             }
//             if (status) {
//                 fields.push('status = ?');
//                 values.push(status);
//             }

//             if (fields.length === 0) {
//                 throw new Error('Không có trường nào để cập nhật');
//             }

//             values.push(id);
//             console.log('Updating evaluation with values:', values);
//             const query = `UPDATE evaluations SET ${fields.join(', ')} WHERE id = ?`;
//             const [result] = await db.execute(query, values);
//             return result.affectedRows > 0;
//         } catch (error) {
//             throw new Error(`Không thể cập nhật đánh giá: ${error.message}`);
//         }
//     }

//     static async delete(id) {
//         try {
//             const [result] = await db.execute('DELETE FROM evaluations WHERE id = ?', [id]);
//             return result.affectedRows > 0;
//         } catch (error) {
//             throw new Error(`Không thể xóa đánh giá: ${error.message}`);
//         }
//     }
// }

// module.exports = Evaluation;


const db = require('../config/db');
const moment = require('moment-timezone');

class Evaluation {
    static async create(evaluationData) {
        try {
            const {
                employee_id, evaluator_id, evaluation_date, performance_score,
                kpi_score, overall_score, strengths, improvement_areas, comments, status
            } = evaluationData;

            const query = `
                INSERT INTO evaluations (
                    employee_id, evaluator_id, evaluation_date, performance_score,
                    kpi_score, overall_score, strengths, improvement_areas, comments, status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            `;
            const values = [
                employee_id, evaluator_id, moment.utc(evaluation_date).format('YYYY-MM-DD'),
                performance_score, kpi_score, overall_score, strengths || null,
                improvement_areas || null, comments || null, status || 'draft'
            ];
            console.log('Creating evaluation with values:', values);
            const [result] = await db.execute(query, values);
            return result.insertId ? await this.findById(result.insertId) : null;
        } catch (error) {
            throw new Error(`Không thể tạo đánh giá: ${error.message}`);
        }
    }

    static async findById(id) {
        try {
            const [rows] = await db.execute(`
                SELECT e.*, 
                       CONCAT(emp.first_name, ' ', emp.last_name) as employee_name,
                       CONCAT(eva.first_name, ' ', eva.last_name) as evaluator_name,
                       d.name as department_name
                FROM evaluations e
                LEFT JOIN employees emp ON e.employee_id = emp.id
                LEFT JOIN employees eva ON e.evaluator_id = eva.id
                LEFT JOIN departments d ON emp.department_id = d.id
                WHERE e.id = ?
            `, [id]);
            return rows[0] || null;
        } catch (error) {
            throw new Error(`Không thể tìm đánh giá: ${error.message}`);
        }
    }

    static async getAll({ employee_id, evaluator_id, start_date, end_date, status, department_id, page = 1, limit = 10 }) {
        try {
            const offset = (page - 1) * limit;
            let query = `
                SELECT e.*, 
                       CONCAT(emp.first_name, ' ', emp.last_name) as employee_name,
                       CONCAT(eva.first_name, ' ', eva.last_name) as evaluator_name,
                       d.name as department_name
                FROM evaluations e
                LEFT JOIN employees emp ON e.employee_id = emp.id
                LEFT JOIN employees eva ON e.evaluator_id = eva.id
                LEFT JOIN departments d ON emp.department_id = d.id
                WHERE 1=1
            `;
            const values = [];

            if (employee_id) {
                query += ' AND e.employee_id = ?';
                values.push(employee_id);
            }
            if (evaluator_id) {
                query += ' AND e.evaluator_id = ?';
                values.push(evaluator_id);
            }
            if (start_date) {
                query += ' AND e.evaluation_date >= ?';
                values.push(moment.utc(start_date).format('YYYY-MM-DD'));
            }
            if (end_date) {
                query += ' AND e.evaluation_date <= ?';
                values.push(moment.utc(end_date).format('YYYY-MM-DD'));
            }
            if (status) {
                query += ' AND e.status = ?';
                values.push(status);
            }
            if (department_id) {
                query += ' AND emp.department_id = ?';
                values.push(department_id);
            }

            query += ' ORDER BY e.evaluation_date DESC LIMIT ? OFFSET ?';
            values.push(limit, offset);

            const [rows] = await db.execute(query, values);

            const [countResult] = await db.execute(`
                SELECT COUNT(*) as total
                FROM evaluations e
                LEFT JOIN employees emp ON e.employee_id = emp.id
                WHERE 1=1
                ${employee_id ? 'AND e.employee_id = ?' : ''}
                ${evaluator_id ? 'AND e.evaluator_id = ?' : ''}
                ${start_date ? 'AND e.evaluation_date >= ?' : ''}
                ${end_date ? 'AND e.evaluation_date <= ?' : ''}
                ${status ? 'AND e.status = ?' : ''}
                ${department_id ? 'AND emp.department_id = ?' : ''}
            `, values.slice(0, -2));

            return {
                records: rows,
                total: countResult[0].total,
                page,
                limit
            };
        } catch (error) {
            throw new Error(`Không thể lấy danh sách đánh giá: ${error.message}`);
        }
    }

    static async update(id, evaluationData) {
        try {
            const {
                evaluation_date, performance_score, kpi_score, overall_score,
                strengths, improvement_areas, comments, status
            } = evaluationData;

            const fields = [];
            const values = [];

            if (evaluation_date) {
                fields.push('evaluation_date = ?');
                values.push(moment.utc(evaluation_date).format('YYYY-MM-DD'));
            }
            if (performance_score !== undefined) {
                fields.push('performance_score = ?');
                values.push(performance_score);
            }
            if (kpi_score !== undefined) {
                fields.push('kpi_score = ?');
                values.push(kpi_score);
            }
            if (overall_score !== undefined) {
                fields.push('overall_score = ?');
                values.push(overall_score);
            }
            if (strengths !== undefined) {
                fields.push('strengths = ?');
                values.push(strengths || null);
            }
            if (improvement_areas !== undefined) {
                fields.push('improvement_areas = ?');
                values.push(improvement_areas || null);
            }
            if (comments !== undefined) {
                fields.push('comments = ?');
                values.push(comments || null);
            }
            if (status) {
                fields.push('status = ?');
                values.push(status);
            }

            if (fields.length === 0) {
                throw new Error('Không có trường nào để cập nhật');
            }

            values.push(id);
            console.log('Updating evaluation with values:', values);
            const query = `UPDATE evaluations SET ${fields.join(', ')} WHERE id = ?`;
            const [result] = await db.execute(query, values);
            return result.affectedRows > 0;
        } catch (error) {
            throw new Error(`Không thể cập nhật đánh giá: ${error.message}`);
        }
    }

    static async delete(id) {
        try {
            const [result] = await db.execute('DELETE FROM evaluations WHERE id = ?', [id]);
            return result.affectedRows > 0;
        } catch (error) {
            throw new Error(`Không thể xóa đánh giá: ${error.message}`);
        }
    }
}

module.exports = Evaluation;