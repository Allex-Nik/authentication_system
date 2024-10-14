const API_BASE_URL = '';

// Function for user registration
async function handleRegister(event) {
    event.preventDefault();
    const form = event.target;
    const data = {
        username: form.username.value,
        email: form.email.value,
        password: form.password.value,
        firstName: form.firstName.value,
        lastName: form.lastName.value,
    };

    try {
        const response = await fetch(`/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });
        const message = await response.text();
        document.getElementById('message').innerText = message;
        if (response.ok) {
            // Redirect to login page
            window.location.href = 'login.html';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Function for login
async function handleLogin(event) {
    event.preventDefault();
    const form = event.target;
    const data = {
        username: form.username.value,
        password: form.password.value,
    };

    try {
        const response = await fetch(`/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });
        if (response.ok) {
            const result = await response.json();
            // Saving token to localStorage
            localStorage.setItem('token', result.token);
            // Redirect to the user's page
            window.location.href = 'user.html';
        } else {
            const message = await response.text();
            document.getElementById('message').innerText = message;
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Function for getting user information
async function loadUserInfo() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(`/user`, {
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            const user = await response.json();
            const userInfoDiv = document.getElementById('user-info');
            userInfoDiv.innerHTML = `
                <p>Username: ${user.username}</p>
                <p>Email: ${user.email}</p>
                <p>Name: ${user.firstName || ''}</p>
                <p>Surname: ${user.lastName || ''}</p>
            `;
        } else {
            localStorage.removeItem('token');
            window.location.href = 'login.html';
        }
    } catch (error) {
        console.error('Error:', error);
        localStorage.removeItem('token');
        window.location.href = 'login.html';
    }
}

// Function for logout
async function handleLogout() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(`/auth/logout`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            localStorage.removeItem('token');
            window.location.href = 'login.html';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Function for account deletion
async function handleDeleteAccount() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch(`/user`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` },
        });
        if (response.ok) {
            localStorage.removeItem('token');
            window.location.href = 'index.html';
        } else {
            const message = await response.text();
            document.getElementById('message').innerText = message;
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Event handlers
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('register-form')) {
        document.getElementById('register-form').addEventListener('submit', handleRegister);
    }

    if (document.getElementById('login-form')) {
        document.getElementById('login-form').addEventListener('submit', handleLogin);
    }

    if (document.getElementById('user-info')) {
        loadUserInfo();
    }

    if (document.getElementById('logout-button')) {
        document.getElementById('logout-button').addEventListener('click', handleLogout);
    }

    if (document.getElementById('delete-account-button')) {
            document.getElementById('delete-account-button').addEventListener('click', handleDeleteAccount);
        }
});
