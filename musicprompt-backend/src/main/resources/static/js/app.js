const API_BASE = '/api';

let currentToken = localStorage.getItem('token');
let currentUser = localStorage.getItem('username');

document.addEventListener('DOMContentLoaded', function() {
    checkBackendStatus();
    if (currentToken && currentUser) {
        showMainContent();
    } else {
        showPublicContent();
    }
});

function showLogin() {
    hideAllForms();
    document.getElementById('login-form').style.display = 'block';
}

function showRegister() {
    hideAllForms();
    document.getElementById('register-form').style.display = 'block';
}

function hideAuthForms() {
    hideAllForms();
    if (currentToken) {
        showMainContent();
    } else {
        showPublicContent();
    }
}

function hideAllForms() {
    document.getElementById('login-form').style.display = 'none';
    document.getElementById('register-form').style.display = 'none';
    document.getElementById('main-content').style.display = 'none';
    document.getElementById('public-content').style.display = 'none';
}

function showMainContent() {
    hideAllForms();
    document.getElementById('main-content').style.display = 'block';
    document.getElementById('user-info').innerHTML = `
        <p>Welcome, <strong>${currentUser}</strong>!</p>
        <p>You are successfully logged in.</p>
    `;
    document.getElementById('login-btn').style.display = 'none';
    document.getElementById('register-btn').style.display = 'none';
    document.getElementById('logout-btn').style.display = 'inline-block';
}

function showPublicContent() {
    hideAllForms();
    document.getElementById('public-content').style.display = 'block';
    document.getElementById('login-btn').style.display = 'inline-block';
    document.getElementById('register-btn').style.display = 'inline-block';
    document.getElementById('logout-btn').style.display = 'none';
}

async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (response.ok) {
            currentToken = data.token;
            currentUser = username;

            localStorage.setItem('token', currentToken);
            localStorage.setItem('username', currentUser);
            localStorage.setItem('refreshToken', data.refreshToken);

            showMessage('Login successful!', 'success');
            showMainContent();
        } else {
            showMessage(data.message || 'Login failed', 'error');
        }
    } catch (error) {
        showMessage('Network error: ' + error.message, 'error');
    }
}

async function handleRegister(event) {
    event.preventDefault();

    const username = document.getElementById('register-username').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ username, email, password })
        });

        const data = await response.json();

        if (response.ok) {
            showMessage('Registration successful! Please login.', 'success');
            showLogin();
            document.getElementById('register-form').querySelector('form').reset();
        } else {
            showMessage(data.message || 'Registration failed', 'error');
        }
    } catch (error) {
        showMessage('Network error: ' + error.message, 'error');
    }
}

async function logout() {
    try {
        if (currentToken) {
            const response = await fetch(`${API_BASE}/auth/logout`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${currentToken}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                showMessage('Logout successful!', 'success');
            }
        }
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('refreshToken');
        currentToken = null;
        currentUser = null;

        showPublicContent();
        document.getElementById('login-form').querySelector('form').reset();
        document.getElementById('register-form').querySelector('form').reset();
    }
}

async function testProtectedEndpoint() {
    if (!currentToken) {
        showMessage('Please login first', 'error');
        return;
    }

    try {
        const response = await fetch('/api/health/check', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${currentToken}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            document.getElementById('api-response').innerHTML = `
                <div class="message success">
                    <p><strong>✅ Protected Endpoint Accessible!</strong></p>
                    <p><strong>Status:</strong> ${response.status}</p>
                    <pre>${JSON.stringify(data, null, 2)}</pre>
                </div>
            `;
        } else {
            document.getElementById('api-response').innerHTML = `
                <div class="message error">
                    <p><strong>❌ Error:</strong> ${response.status} - ${response.statusText}</p>
                </div>
            `;
        }
    } catch (error) {
        document.getElementById('api-response').innerHTML = `
            <div class="message error">
                <p><strong>❌ Network Error:</strong> ${error.message}</p>
            </div>
        `;
    }
}

async function testPublicEndpoint() {
    try {
        const response = await fetch('/api/health/public');

        if (response.ok) {
            const data = await response.json();
            document.getElementById('api-response').innerHTML = `
                <div class="message success">
                    <p><strong>✅ Public Endpoint Accessible!</strong></p>
                    <p><strong>Status:</strong> ${response.status}</p>
                    <pre>${JSON.stringify(data, null, 2)}</pre>
                </div>
            `;
        } else {
            document.getElementById('api-response').innerHTML = `
                <div class="message error">
                    <p><strong>❌ Error:</strong> ${response.status} - ${response.statusText}</p>
                </div>
            `;
        }
    } catch (error) {
        document.getElementById('api-response').innerHTML = `
            <div class="message error">
                <p><strong>❌ Network Error:</strong> ${error.message}</p>
            </div>
        `;
    }
}

async function checkBackendStatus() {
    try {
        const response = await fetch('/actuator/health');
        const data = await response.json();

        const statusElement = document.getElementById('backend-status');
        if (statusElement) {
            if (data.status === 'UP') {
                statusElement.innerHTML = '✅ Backend is running';
                statusElement.className = 'status up';
            } else {
                statusElement.innerHTML = '❌ Backend has issues';
                statusElement.className = 'status down';
            }
        }
    } catch (error) {
        const statusElement = document.getElementById('backend-status');
        if (statusElement) {
            statusElement.innerHTML = '❌ Cannot connect to backend';
            statusElement.className = 'status down';
        }
    }
}

function showMessage(message, type) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;

    const main = document.querySelector('main');
    if (main) {
        main.insertBefore(messageDiv, main.firstChild);

        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 5000);
    }
}

setInterval(checkBackendStatus, 30000);