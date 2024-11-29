// src/Services/CountryService.js
import axios from "axios";

const COUNTRY_API_URL = "https://api.countrystatecity.in/v1/countries";
const API_KEY = "YOUR_API_KEY"; // Replace with your actual API key

const CountryService = {
  getCountries: async () => {
    try {
      const response = await axios.get(COUNTRY_API_URL, {
        headers: {
          "X-CSCAPI-KEY": API_KEY,
        },
      });
      return response.data; // Return the country data
    } catch (error) {
      throw new Error(
        error.response ? error.response.data.message : error.message
      );
    }
  },
};

export default CountryService;
