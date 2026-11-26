/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      colors: {
        night: '#0b0b0f',   // page background
        crypt: '#16161d',   // surfaces
        pumpkin: '#ff6b00', // primary
        blood: '#8b0000',   // danger / cursed
        ecto: '#39ff14',    // accents only
        bone: '#e8e6e3',    // text
        mist: '#a8a6a3',    // secondary text
      },
      fontFamily: {
        creep: ['Creepster', 'cursive'],
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
