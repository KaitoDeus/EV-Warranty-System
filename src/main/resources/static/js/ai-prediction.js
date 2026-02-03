/**
 * AI Failure Prediction Module JavaScript
 */

document.addEventListener('DOMContentLoaded', function() {
    const predictionContainer = document.getElementById('ai-prediction-results');
    if (!predictionContainer) return;

    const vehicleId = predictionContainer.dataset.vehicleId;
    fetchPredictions(vehicleId);
});

async function fetchPredictions(vehicleId) {
    const container = document.getElementById('ai-prediction-results');
    
    try {
        const result = await API.AI.getPredictions(vehicleId);
        
        if (result.success && result.data) {
            if (result.data.length === 0) {
                renderNoPredictions();
            } else {
                renderPredictions(result.data);
            }
        } else {
            throw new Error('API failed');
        }
    } catch (error) {
        console.error('AI Prediction error:', error);
        container.innerHTML = `
            <div class="no-prediction text-danger">
                <i class="fas fa-exclamation-triangle"></i>
                <p>Không thể tải dự báo từ AI lúc này. Vui lòng thử lại sau.</p>
            </div>
        `;
    }
}

function renderPredictions(predictions) {
    const container = document.getElementById('ai-prediction-results');
    
    let html = '<div class="prediction-grid">';
    
    predictions.forEach(p => {
        const riskLevelClass = `risk-${p.riskLevel.toLowerCase()}`;
        const probabilityPct = Math.round(p.failureProbability * 100);
        const riskLevelLabel = getRiskLevelLabel(p.riskLevel);
        
        html += `
            <div class="prediction-card ${riskLevelClass}">
                <div class="prediction-meta">
                    <span class="badge ${getBadgeClass(p.riskLevel)}">${riskLevelLabel}</span>
                    <span>Xác suất: ${probabilityPct}%</span>
                </div>
                <div class="prediction-title">${p.partName}</div>
                <div class="probability-bar-container">
                    <div class="probability-bar" style="width: ${probabilityPct}%"></div>
                </div>
                <div class="prediction-action">
                    <i class="fas fa-robot"></i>
                    <span>${p.recommendedAction}</span>
                </div>
            </div>
        `;
    });
    
    html += '</div>';
    container.innerHTML = html;
}

function renderNoPredictions() {
    const container = document.getElementById('ai-prediction-results');
    container.innerHTML = `
        <div class="no-prediction">
            <i class="fas fa-shield-check"></i>
            <p>Không phát hiện rủi ro lỗi phần cứng đáng kể cho dòng xe này tại thời điểm hiện tại.</p>
        </div>
    `;
}

function getBadgeClass(riskLevel) {
    switch(riskLevel) {
        case 'CRITICAL': return 'badge-danger';
        case 'HIGH': return 'badge-warning';
        case 'MEDIUM': return 'badge-info';
        case 'LOW': return 'badge-success';
        default: return 'badge-secondary';
    }
}

function getRiskLevelLabel(riskLevel) {
    switch(riskLevel) {
        case 'CRITICAL': return 'RỦI RO NGUY CẤP';
        case 'HIGH': return 'RỦI RO CAO';
        case 'MEDIUM': return 'RỦI RO TRUNG BÌNH';
        case 'LOW': return 'RỦI RO THẤP';
        default: return 'KHÔNG XÁC ĐỊNH';
    }
}
