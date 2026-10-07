document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('loginForm');
  const registerForm = document.getElementById('registerForm');
  const logoutBtn = document.getElementById('logoutBtn');

  if (loginForm) {
    loginForm.addEventListener('submit', async (event) => {
      event.preventDefault();

      const username = document.getElementById('loginUsername').value.trim();
      const password = document.getElementById('loginPassword').value;
      const errorNode = document.getElementById('loginError');

      if (!username || !password) {
        errorNode.textContent = 'Please enter both username and password.';
        return;
      }

      try {
        const result = await apiRequest('/login', {
          method: 'POST',
          body: JSON.stringify({ username, password }),
        });

        localStorage.setItem('resumeMatcherToken', result.token || '');
        localStorage.setItem('resumeMatcherUsername', username);
        window.location.href = 'dashboard.html';
      } catch (error) {
        errorNode.textContent = error.message || 'Login failed.';
      }
    });
  }

  if (registerForm) {
    registerForm.addEventListener('submit', async (event) => {
      event.preventDefault();

      const username = document.getElementById('registerUsername').value.trim();
      const password = document.getElementById('registerPassword').value;
      const confirmPassword = document.getElementById('registerConfirmPassword').value;
      const role = document.getElementById('registerRole').value;
      const errorNode = document.getElementById('registerError');
      const successNode = document.getElementById('registerSuccess');

      errorNode.textContent = '';
      successNode.textContent = '';

      if (!username || !password || !confirmPassword) {
        errorNode.textContent = 'Please complete all required fields.';
        return;
      }

      if (password.length < 6) {
        errorNode.textContent = 'Password must be at least 6 characters long.';
        return;
      }

      if (password !== confirmPassword) {
        errorNode.textContent = 'Passwords do not match.';
        return;
      }

      try {
        const result = await apiRequest('/register', {
          method: 'POST',
          body: JSON.stringify({ username, password, role }),
        });

        successNode.textContent = 'Account created successfully. You can now sign in.';
        registerForm.reset();
        setTimeout(() => {
          window.location.href = 'login.html';
        }, 800);
      } catch (error) {
        errorNode.textContent = error.message || 'Registration failed.';
      }
    });
  }

  if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
      try {
        await apiRequest('/logout', { method: 'POST' });
      } catch (error) {
        console.warn('Logout request failed:', error);
      } finally {
        logoutClient();
      }
    });
  }

  if (window.location.pathname.endsWith('dashboard.html')) {
    requireAuth();
  }
});
