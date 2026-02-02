/**
 * Claims Page - AJAX functionality
 * Demonstrates API usage with jQuery for Warranty Claims
 */
$(function() {
    'use strict';

    // =====================
    // Quick Actions
    // =====================
    
    /**
     * Submit claim via AJAX
     */
    $(document).on('click', '.btn-submit-claim', function(e) {
        e.preventDefault();
        const claimId = $(this).data('claim-id');
        
        if (!API.UI.confirm('Bạn có chắc muốn gửi yêu cầu bảo hành này?')) {
            return;
        }

        const $btn = $(this);
        API.UI.showLoading($btn);

        API.Claims.submit(claimId)
            .then(function(response) {
                API.UI.showSuccess('Yêu cầu đã được gửi thành công!');
                // Reload page after 1 second
                setTimeout(function() {
                    window.location.reload();
                }, 1000);
            })
            .catch(function(error) {
                API.UI.showError(error.message || 'Có lỗi xảy ra khi gửi yêu cầu');
            })
            .always(function() {
                API.UI.hideLoading($btn);
            });
    });

    /**
     * Delete claim via AJAX
     */
    $(document).on('click', '.btn-delete-claim', function(e) {
        e.preventDefault();
        const claimId = $(this).data('claim-id');
        
        if (!API.UI.confirm('Bạn có chắc muốn xóa yêu cầu này? Hành động này không thể hoàn tác.')) {
            return;
        }

        const $btn = $(this);
        API.UI.showLoading($btn);

        API.Claims.delete(claimId)
            .then(function(response) {
                API.UI.showSuccess('Yêu cầu đã được xóa!');
                // Remove row from table
                $btn.closest('tr').fadeOut(function() {
                    $(this).remove();
                });
            })
            .catch(function(error) {
                API.UI.showError(error.message || 'Có lỗi xảy ra khi xóa yêu cầu');
            })
            .always(function() {
                API.UI.hideLoading($btn);
            });
    });

    // =====================
    // Live Search
    // =====================
    let searchTimeout = null;
    
    $('#live-search-input').on('input', function() {
        const query = $(this).val();
        
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(function() {
            loadClaimsData(0, 10, query);
        }, 300);
    });

    /**
     * Load claims via AJAX (for dynamic table updates)
     */
    function loadClaimsData(page, size, search) {
        const $table = $('#claims-table tbody');
        $table.html('<tr><td colspan="6" class="text-center"><i class="fas fa-spinner fa-spin"></i> Đang tải...</td></tr>');

        API.Claims.getAll(page, size, search)
            .then(function(response) {
                renderClaimsTable(response.data);
            })
            .catch(function(error) {
                $table.html('<tr><td colspan="6" class="text-center text-danger">Lỗi: ' + error.message + '</td></tr>');
            });
    }

    /**
     * Render claims table rows
     */
    function renderClaimsTable(claims) {
        const $tbody = $('#claims-table tbody');
        $tbody.empty();

        if (!claims || claims.length === 0) {
            $tbody.html('<tr><td colspan="6" class="text-center text-muted">Không tìm thấy yêu cầu nào</td></tr>');
            return;
        }

        claims.forEach(function(claim) {
            const statusClass = 'status-' + claim.status.toLowerCase().replace('_', '-');
            const row = `
                <tr data-claim-id="${claim.id}">
                    <td><a href="/sc/claims/${claim.id}">${claim.claimNumber}</a></td>
                    <td>${claim.vehicle ? claim.vehicle.vin : '-'}</td>
                    <td><span class="badge ${statusClass}">${claim.status}</span></td>
                    <td>${truncate(claim.failureDescription, 40)}</td>
                    <td>${API.UI.formatDate(claim.createdAt)}</td>
                    <td>
                        <a href="/sc/claims/${claim.id}" class="btn btn-sm btn-secondary" title="Xem chi tiết">
                            <i class="fas fa-eye"></i>
                        </a>
                        ${claim.status === 'DRAFT' ? `
                            <button class="btn btn-sm btn-primary btn-submit-claim" data-claim-id="${claim.id}" title="Gửi yêu cầu">
                                <i class="fas fa-paper-plane"></i>
                            </button>
                            <button class="btn btn-sm btn-danger btn-delete-claim" data-claim-id="${claim.id}" title="Xóa">
                                <i class="fas fa-trash"></i>
                            </button>
                        ` : ''}
                    </td>
                </tr>
            `;
            $tbody.append(row);
        });
    }

    /**
     * Utility: Truncate string
     */
    function truncate(str, len) {
        if (!str) return '-';
        return str.length > len ? str.substring(0, len) + '...' : str;
    }

    // =====================
    // Claim Form Enhancements
    // =====================
    
    /**
     * Vehicle VIN lookup
     */
    $('#vin-lookup').on('click', function() {
        const vin = $('#vehicle-vin').val().trim();
        
        if (!vin) {
            API.UI.showError('Vui lòng nhập số VIN');
            return;
        }

        const $btn = $(this);
        API.UI.showLoading($btn);

        API.Vehicles.getByVin(vin)
            .then(function(response) {
                if (response.data) {
                    const vehicle = response.data;
                    $('#vehicle-info').html(`
                        <div class="alert alert-info">
                            <strong>Xe:</strong> ${vehicle.make} ${vehicle.model} (${vehicle.year})<br>
                            <strong>Chủ xe:</strong> ${vehicle.customer ? vehicle.customer.fullName : 'Chưa gán'}<br>
                            <strong>Bảo hành:</strong> ${API.UI.formatDate(vehicle.warrantyStartDate)} - ${API.UI.formatDate(vehicle.warrantyEndDate)}
                        </div>
                    `);
                    $('#vehicle-id').val(vehicle.id);
                } else {
                    API.UI.showError('Không tìm thấy xe với VIN: ' + vin);
                }
            })
            .catch(function(error) {
                API.UI.showError('Lỗi tra cứu: ' + error.message);
            })
            .always(function() {
                API.UI.hideLoading($btn);
            });
    });

    // =====================
    // EVM Staff Actions
    // =====================
    
    /**
     * Approve claim (for EVM Staff)
     */
    $(document).on('click', '.btn-approve-claim', function(e) {
        e.preventDefault();
        const claimId = $(this).data('claim-id');
        const reviewerId = $(this).data('reviewer-id');
        
        if (!API.UI.confirm('Bạn có chắc muốn PHÊ DUYỆT yêu cầu này?')) {
            return;
        }

        const $btn = $(this);
        API.UI.showLoading($btn);

        API.Claims.approve(claimId, reviewerId)
            .then(function(response) {
                API.UI.showSuccess('Yêu cầu đã được phê duyệt!');
                setTimeout(function() { window.location.reload(); }, 1000);
            })
            .catch(function(error) {
                API.UI.showError(error.message || 'Có lỗi xảy ra');
            })
            .always(function() {
                API.UI.hideLoading($btn);
            });
    });

    /**
     * Reject claim (for EVM Staff)
     */
    $(document).on('click', '.btn-reject-claim', function(e) {
        e.preventDefault();
        const claimId = $(this).data('claim-id');
        const reviewerId = $(this).data('reviewer-id');
        const reason = prompt('Nhập lý do từ chối:');
        
        if (!reason || reason.trim() === '') {
            API.UI.showError('Vui lòng nhập lý do từ chối');
            return;
        }

        const $btn = $(this);
        API.UI.showLoading($btn);

        API.Claims.reject(claimId, reviewerId, reason)
            .then(function(response) {
                API.UI.showSuccess('Yêu cầu đã bị từ chối');
                setTimeout(function() { window.location.reload(); }, 1000);
            })
            .catch(function(error) {
                API.UI.showError(error.message || 'Có lỗi xảy ra');
            })
            .always(function() {
                API.UI.hideLoading($btn);
            });
    });

});
