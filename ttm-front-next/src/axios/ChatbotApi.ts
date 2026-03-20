import axios from "axios";

/**
 * Sends a message and optional file to the backend to get a suggested recipe.
 * @param {string} message - The user's input message.
 * @param {File | null} file - An optional file to include in the prompt.
 * @returns {Promise<string>} The AI-generated recipe response.
 */
export async function suggestRecipe(
  message: string,
  file: File | null = null
): Promise<string> {
  try {
    const formData = new FormData();
    formData.append("message", message);
    if (file) {
      formData.append("file", file);
    }

    const response = await axios.post("http://localhost:8079/recipes/suggest", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    });

    // Adjust to match the actual structure of your backend's response
    return response.data.response || JSON.stringify(response.data);
  } catch (error) {
    console.error("Error suggesting recipe:", error);
    throw error;
  }
}
