export const environment = {
  production: true,
  // Netlify's build command replaces __API_URL__ with the real Render URL before ng build runs.
  // See netlify.toml for the sed command.
  apiUrl: '__API_URL__',
};
