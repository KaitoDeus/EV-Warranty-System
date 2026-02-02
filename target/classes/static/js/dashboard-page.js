/**
 * Dashboard Page - Real-time statistics
 * Uses API client to fetch live dashboard data
 */
$(function() {
    'use strict';

    // Load dashboard stats on page load
    loadDashboardStats();

    // Refresh every 30 seconds
    setInterval(loadDashboardStats, 30000);

    /**
     * Load dashboard statistics from API
     */
    function loadDashboardStats() {
        API.Reports.getDashboard()
            .then(function(response) {
                updateDashboardCards(response.data);
            })
            .catch(function(error) {
                console.error('Failed to load dashboard stats:', error);
            });
    }

    /**
     * Update dashboard stat cards
     */
    function updateDashboardCards(stats) {
        // Animate number updates
        animateValue('#stat-total-vehicles', stats.totalVehicles);
        animateValue('#stat-total-customers', stats.totalCustomers);
        animateValue('#stat-total-claims', stats.totalClaims);
        animateValue('#stat-pending-claims', stats.pendingClaims);
        animateValue('#stat-active-campaigns', stats.activeCampaigns);
        animateValue('#stat-completed-claims', stats.completedClaims);
    }

    /**
     * Animate number value change
     */
    function animateValue(selector, newValue) {
        const $el = $(selector);
        if ($el.length === 0) return;
        
        const currentValue = parseInt($el.text()) || 0;
        if (currentValue === newValue) return;

        $({ val: currentValue }).animate({ val: newValue }, {
            duration: 500,
            easing: 'swing',
            step: function() {
                $el.text(Math.floor(this.val));
            },
            complete: function() {
                $el.text(newValue);
            }
        });
    }

    // =====================
    // Claims Summary Chart
    // =====================
    
    /**
     * Load claims by status for chart
     */
    function loadClaimsSummary() {
        API.Reports.getClaimsSummary()
            .then(function(response) {
                renderClaimsSummary(response.data);
            })
            .catch(function(error) {
                console.error('Failed to load claims summary:', error);
            });
    }

    /**
     * Render claims summary (simple bar or list)
     */
    function renderClaimsSummary(data) {
        const $container = $('#claims-summary');
        if ($container.length === 0) return;

        const total = data.total || 1;
        const items = [
            { label: 'Nháp', value: data.draft || 0, color: '#6c757d' },
            { label: 'Đã gửi', value: data.submitted || 0, color: '#0d6efd' },
            { label: 'Đã duyệt', value: data.approved || 0, color: '#198754' },
            { label: 'Từ chối', value: data.rejected || 0, color: '#dc3545' },
            { label: 'Đang xử lý', value: data.inProgress || 0, color: '#ffc107' },
            { label: 'Hoàn tất', value: data.completed || 0, color: '#20c997' }
        ];

        let html = '<div class="claims-summary-bars">';
        items.forEach(function(item) {
            const percent = Math.round((item.value / total) * 100);
            html += `
                <div class="summary-item">
                    <span class="label">${item.label}</span>
                    <div class="bar-container">
                        <div class="bar" style="width: ${percent}%; background: ${item.color};"></div>
                    </div>
                    <span class="value">${item.value}</span>
                </div>
            `;
        });
        html += '</div>';
        
        $container.html(html);
    }

    // Initial load
    loadClaimsSummary();
});
