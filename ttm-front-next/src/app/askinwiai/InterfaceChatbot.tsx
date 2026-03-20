"use client";

import Image from "next/image";
import { useState } from "react";
import { suggestRecipe } from "@/axios/ChatbotApi";

type ChatMessage = {
  type: "user" | "bot";
  text: string;
};

export default function InterfaceChatbot() {
  const [message, setMessage] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const handleSendMessage = async () => {
    if (!message.trim()) return;

    const updatedMessages: ChatMessage[] = [
      ...messages,
      { type: "user", text: message },
    ];
    setMessages(updatedMessages);
    setMessage("");
    setIsLoading(true);

    try {
      const response = await suggestRecipe(message, file);
      setMessages([
        ...updatedMessages,
        {
          type: "bot",
          text: response || "No response from model.",
        },
      ]);
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      setMessages([
        ...updatedMessages,
        { type: "bot", text: "Sorry, something went wrong." },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-pink-100 via-pink-200 to-pink-100 p-6">
      <div className="mb-6">
        <Image
          src="/assets/chatbot.png"
          alt="Chatbot Agent"
          width={150}
          height={150}
          className="rounded-full border-4 border-black-300 shadow-md"
        />
      </div>

      <div className="w-full max-w-2xl bg-white p-6 rounded-xl shadow-xl space-y-4">
        <div className="h-96 overflow-y-auto border border-gray-200 rounded p-4 text-base text-gray-800 space-y-4">
          {messages.length === 0 ? (
            <p className="text-center text-gray-400">Conversation will appear here</p>
          ) : (
            messages.map((msg, idx) =>
              msg.type === "user" ? (
                <div key={idx} className="flex justify-end">
                  <p className="bg-gray-200 p-3 rounded-xl max-w-[80%] text-right">
                    {msg.text}
                  </p>
                </div>
              ) : (
                <div key={idx} className="flex items-start gap-2">
                  <Image
                    src="/assets/chatbot.png"
                    alt="Bot Icon"
                    width={30}
                    height={30}
                    className="rounded-full border border-gray-300"
                  />
                  <div className="bg-pink-100 p-3 rounded-xl max-w-[80%] text-left whitespace-pre-wrap prose prose-sm">
                    {msg.text}
                  </div>
                </div>
              )
            )
          )}
          {isLoading && (
            <div className="text-[#BB2C63] font-semibold animate-pulse mt-2">
              INWI AI is Typing<span className="animate-bounce inline-block">...</span>
            </div>
          )}
        </div>

        <label className="block">
          <span className="sr-only">Choose file</span>
          <input
            type="file"
            className="block w-full text-sm text-gray-500
              file:mr-4 file:py-2 file:px-4
              file:rounded-full file:border-0
              file:text-sm file:font-semibold
              file:bg-[#780253] file:text-white
              hover:file:bg-pink-700"
            onChange={(e) => {
              const selectedFile = e.target.files?.[0];
              setFile(selectedFile || null);
            }}
          />
        </label>

        <div className="flex items-center gap-2">
          <input
            type="text"
            placeholder="Ask INWI AI..."
            className="flex-1 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#BB2C63] text-base text-black"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") handleSendMessage();
            }}
          />

          <button
            className="bg-[#780253] text-white px-5 py-3 rounded-lg hover:bg-pink-700 transition"
            onClick={handleSendMessage}
            disabled={isLoading}
          >
            {isLoading ? "..." : "Send"}

            
          </button>
        </div>
      </div>
    </div>
  );
}
