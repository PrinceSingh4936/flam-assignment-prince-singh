const file = document.getElementById('file');
const img = document.getElementById('img');
const stats = document.getElementById('stats');

file.addEventListener('change', (e) => {
  const f = e.target.files[0];
  if (!f) return;
  const reader = new FileReader();
  reader.onload = () => {
    img.src = reader.result;
    img.onload = () => {
      stats.textContent = `FPS: simulated | Resolution: ${img.naturalWidth}x${img.naturalHeight}`;
    }
  };
  reader.readAsDataURL(f);
});
