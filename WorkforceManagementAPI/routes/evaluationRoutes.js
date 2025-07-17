// evaluationRoutes.js

const express = require('express');
const router = express.Router();
const evaluationController = require('../controllers/evaluationController');
const { authenticate } = require('../middleware/authMiddleware');
const { createEvaluationValidator, updateEvaluationValidator } = require('../middleware/evaluationMiddleware');

router.get('/', authenticate, evaluationController.getEvaluations);
router.post('/', authenticate, createEvaluationValidator, evaluationController.createEvaluation);
router.put('/:id', authenticate, updateEvaluationValidator, evaluationController.updateEvaluation);
router.delete('/:id', authenticate, evaluationController.deleteEvaluation);

module.exports = router;