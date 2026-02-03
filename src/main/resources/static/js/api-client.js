/**
 * EV Warranty System - API Client
 * Centralized API configuration and methods
 */

const API_BASE_URL = '/api/v1';

const API = {
    // Utility functions
    UI: {
        formatDate: function(dateString) {
            if (!dateString) return '-';
            try {
                const date = new Date(dateString);
                return new Intl.DateTimeFormat('vi-VN').format(date);
            } catch (e) { return dateString; }
        },
        formatDateTime: function(dateString) {
            if (!dateString) return '-';
            try {
                const date = new Date(dateString);
                return new Intl.DateTimeFormat('vi-VN', {
                    year: 'numeric', month: '2-digit', day: '2-digit',
                    hour: '2-digit', minute: '2-digit'
                }).format(date);
            } catch (e) { return dateString; }
        },
        formatCurrency: function(amount) {
            if (amount === null || amount === undefined) return '0 ₫';
            return new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(amount);
        },
        showLoading: function($btn) {
            $btn.prop('disabled', true);
            $btn.data('original-text', $btn.html());
            $btn.html('<i class="fas fa-spinner fa-spin"></i>');
        },
        hideLoading: function($btn) {
            $btn.prop('disabled', false);
            $btn.html($btn.data('original-text'));
        },
        showSuccess: function(msg) {
            alert(msg); // Fallback if toast not available
        },
        showError: function(msg) {
            alert('Lỗi: ' + msg);
        },
        confirm: function(msg) {
            return confirm(msg);
        }
    },

    // HTTP Request Helper
    request: function(endpoint, method = 'GET', data = null) {
        const headers = {};
        
        // Add CSRF Token if available
        const csrfToken = $('meta[name="_csrf"]').attr('content');
        const csrfHeader = $('meta[name="_csrf_header"]').attr('content') || 'X-CSRF-TOKEN';
        
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        const ajaxConfig = {
            url: API_BASE_URL + endpoint,
            type: method,
            headers: headers,
            contentType: 'application/json',
            dataType: 'json'
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            ajaxConfig.data = JSON.stringify(data);
        }

        return $.ajax(ajaxConfig)
            .then(function(response) {
                // If response is a Spring Data Page object, extract content
                let finalData = response;
                if (response && response.content && Array.isArray(response.content)) {
                    finalData = response.content;
                }
                return { success: true, data: finalData };
            })
            .catch(function(xhr) {
                console.error('API Request Failed:', endpoint, xhr);
                let message = 'Lỗi kết nối (' + xhr.status + ')';
                
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                } else if (xhr.responseText) {
                     try {
                         const parsed = JSON.parse(xhr.responseText);
                         if (parsed.message) message = parsed.message;
                     } catch(e) {}
                }
                
                // Return a rejected promise
                return $.Deferred().reject({ message: message, status: xhr.status }).promise();
            });
    },

    // Resources
    Reports: {
        getDashboard: function() {
            return API.request('/reports/dashboard');
        },
        getClaimsSummary: function() {
            return API.request('/reports/claims/status');
        }
    },
    
    Vehicles: {
        getAll: function(page = 0, size = 10) {
            return API.request('/vehicles?page=' + page + '&size=' + size);
        },
        getByVin: function(vin) {
             return API.request('/vehicles/search?vin=' + vin);
        },
        get: function(id) {
            return API.request('/vehicles/' + id);
        }
    },
    
    Claims: {
        getAll: function(page = 0, size = 10, search = '') {
            let url = '/claims?page=' + page + '&size=' + size;
            if (search) url = '/claims/search?query=' + search;
            return API.request(url);
        },
        getPending: function(page = 0, size = 10) {
            return API.request('/claims/pending?page=' + page + '&size=' + size);
        },
        submit: function(id) {
            return API.request('/claims/' + id + '/submit', 'POST');
        },
        delete: function(id) {
            return API.request('/claims/' + id + '/delete', 'POST'); 
        },
        approve: function(id, reviewerId) {
             return API.request('/evm/claims/' + id + '/approve', 'POST');
        },
        reject: function(id, reviewerId, reason) {
             return API.request('/evm/claims/' + id + '/reject?rejectionReason=' + encodeURIComponent(reason), 'POST');
        }
    },
    
    Campaigns: {
        getAll: function(page = 0, size = 10) {
            return API.request('/campaigns?page=' + page + '&size=' + size);
        },
        getActive: function() {
            return API.request('/campaigns/active');
        },
        get: function(id) {
            return API.request('/campaigns/' + id);
        }
    },

    AI: {
        getPredictions: function(vehicleId) {
            return API.request('/ai/predict/' + vehicleId);
        }
    }
};

// Expose globally
window.API = API;
console.log('API Client Initialized');
