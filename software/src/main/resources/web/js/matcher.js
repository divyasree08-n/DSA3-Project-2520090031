document.addEventListener('DOMContentLoaded', () => {
  const matchBtn = document.getElementById('matchBtn');
  const resumeText = document.getElementById('resumeText');
  const jobText = document.getElementById('jobText');
  const emptyState = document.getElementById('emptyState');
  const resultContent = document.getElementById('resultContent');
  const scoreDisplay = document.getElementById('scoreDisplay');
  const matchedList = document.getElementById('matchedList');
  const missingList = document.getElementById('missingList');
  const extraList = document.getElementById('extraList');

  if (!matchBtn) return;

  matchBtn.addEventListener('click', async () => {
    if (!requireAuth()) return;

    const resume = resumeText.value.trim();
    const job = jobText.value.trim();

    if (!resume || !job) {
      setToast('Please provide both resume text and job description.', 'error');
      return;
    }

    matchBtn.disabled = true;
    matchBtn.textContent = 'Matching...';

    try {
      const result = await apiRequest('/match', {
        method: 'POST',
        body: JSON.stringify({ resume, job }),
      });

      const scoreValue = Number(result.score ?? 0) * 100;
      scoreDisplay.textContent = `${scoreValue.toFixed(1)}%`;

      renderSkillList(matchedList, result.matchedSkills || [], 'match');
      renderSkillList(missingList, result.missingSkills || [], 'miss');
      renderSkillList(extraList, result.extraSkills || [], 'match');

      emptyState.classList.add('hidden');
      resultContent.classList.remove('hidden');
      setToast('Match analysis complete.', 'success');
    } catch (error) {
      setToast(error.message || 'Match failed.', 'error');
    } finally {
      matchBtn.disabled = false;
      matchBtn.textContent = 'Find Match';
    }
  });
});

function renderSkillList(listElement, items, variant) {
  listElement.innerHTML = '';

  if (!items || items.length === 0) {
    const li = document.createElement('li');
    li.textContent = 'No skills listed.';
    listElement.appendChild(li);
    return;
  }

  items.forEach((item) => {
    const li = document.createElement('li');
    const badge = document.createElement('span');
    badge.className = `badge ${variant}`;
    badge.textContent = variant === 'match' ? 'Match' : 'Missing';

    li.appendChild(badge);
    li.append(' ' + item);
    listElement.appendChild(li);
  });
}
