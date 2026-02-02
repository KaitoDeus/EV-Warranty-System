/**
 * EV Warranty Management System - Layout JavaScript
 * Handles common functionality like Sidebar, Toasts, and Confirmations.
 */

document.addEventListener('DOMContentLoaded', function () {
    initToasts();
    initConfirmDialogs();
    initSearchDebounce();
    initFormValidation();
    initSidebarToggle();
});

/**
 * Toast Notifications
 */
function initToasts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.animation = 'slideOut 0.3s ease forwards';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });
}

// Add slideOut animation style dynamically
const style = document.createElement('style');
style.textContent = `
    @keyframes slideOut {
        from { opacity: 1; transform: translateX(0); }
        to { opacity: 0; transform: translateX(-10px); }
    }
`;
document.head.appendChild(style);

function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type}`;
    toast.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check' : 'exclamation'}-circle"></i>
        <span>${message}</span>
    `;

    const container = document.querySelector('.content-body') || document.body;
    container.insertBefore(toast, container.firstChild);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 5000);
}

/**
 * Confirm Dialogs
 * Usage: <button data-confirm="Are you sure you want to delete this?">Delete</button>
 */
function initConfirmDialogs() {
    document.querySelectorAll('[data-confirm]').forEach(element => {
        element.addEventListener('click', function (e) {
            const message = this.getAttribute('data-confirm') || 'Bạn có chắc chắn muốn thực hiện hành động này không?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
}

/**
 * Search Debounce
 * Usage: <input class="search-input" ...>
 */
function initSearchDebounce() {
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(input => {
        let timeout;
        input.addEventListener('input', function () {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                this.form.submit();
            }, 500);
        });
    });
}

/**
 * Basic Form Validation
 */
function initFormValidation() {
    const forms = document.querySelectorAll('form[data-validate]');
    forms.forEach(form => {
        form.addEventListener('submit', function (e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    isValid = false;
                    field.classList.add('is-invalid');
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            if (!isValid) {
                e.preventDefault();
                showToast('Vui lòng điền đầy đủ các thông tin bắt buộc', 'error');
            }
        });
    });
}

/**
 * Sidebar Toggle for Mobile
 */
function initSidebarToggle() {
    const sidebar = document.querySelector('.sidebar');
    const toggleBtn = document.querySelector('.sidebar-toggle');

    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });
    }

    // Close sidebar when clicking outside on mobile
    document.addEventListener('click', function (e) {
        if (window.innerWidth <= 768) {
            if (!sidebar.contains(e.target) && !toggleBtn?.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        }
    });
}

/**
 * Utility: Format Currency (VND)
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

/**
 * Utility: Set Button Loading State
 */
function setButtonLoading(button, loading = true) {
    if (loading) {
        button.disabled = true;
        button.dataset.originalText = button.innerHTML;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
    } else {
        button.disabled = false;
        button.innerHTML = button.dataset.originalText;
    }
}
