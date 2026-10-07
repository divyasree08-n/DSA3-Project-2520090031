const API_BASE = '/api';

function setToast(message, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;
  document.body.appendChild(toast);

  requestAnimationFrame(() => {
    toast.classList.add('show');
  });

  setTimeout(() => {
    toast.classList.remove('show');
    setTimeout(() => toast.remove(), 200);
  }, 2500);
}

async function apiRequest(path, options = {}) {
  const defaultHeaders = { 'Content-Type': 'application/json' };
  const token = localStorage.getItem('resumeMatcherToken');

  if (token) {
    defaultHeaders.Authorization = 'Bearer ' + token;
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      ...defaultHeaders,
      ...(options.headers || {}),
    },
  });

  const contentType = response.headers.get('content-type') || '';
  const isJson = contentType.includes('application/json');
  const data = isJson ? await response.json() : await response.text();

  if (!response.ok) {
    const message = (data && data.message) || 'Request failed';
    throw new Error(message);
  }

  return data;
}

function requireAuth() {
  const token = localStorage.getItem('resumeMatcherToken');
  if (!token) {
    window.location.href = 'login.html';
    return false;
  }
  return true;
}

function logoutClient() {
  localStorage.removeItem('resumeMatcherToken');
  localStorage.removeItem('resumeMatcherUsername');
  window.location.href = 'login.html';
}

function updateUserLabel() {
  const userLabel = document.getElementById('userLabel');
  if (!userLabel) return;

  const username = localStorage.getItem('resumeMatcherUsername') || 'Guest';
  userLabel.textContent = `Signed in as ${username}`;
}

document.addEventListener('DOMContentLoaded', () => {
  updateUserLabel();
});
