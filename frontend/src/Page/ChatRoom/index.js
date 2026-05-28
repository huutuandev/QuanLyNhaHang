import React, { useState, useEffect, useRef } from "react";
import { MessageOutlined } from "@ant-design/icons";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { jwtDecode } from "jwt-decode";
import "./ChatRoom.scss";
import { Modal } from "antd";

const WS_URL = "/chat-websocket"; // ✅ khớp với BE

function ChatRoom() {
  const [open, setOpen] = useState(false);
  const [sessionId, setSessionId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [showLoginModal, setShowLoginModal] = useState(false);

  const clientRef = useRef(null);
  const messagesEndRef = useRef(null);

  const token = localStorage.getItem("token") || "";
  let decoded = null;
  let isExpired = false;
  if (token) {
    try {
      decoded = jwtDecode(token);
      if (decoded.exp * 1000 < Date.now()) isExpired = true;
    } catch (e) {
      console.error("❌ Token decode error:", e);
      isExpired = true;
    }
  }
  const phoneNumber = decoded?.phoneNumber;

  // Chuẩn hoá field từ server để FE luôn dùng 1 kiểu
  const normalize = (m) => ({
    id: m.id ?? m.messageId,
    sessionId: m.sessionId,
    senderPhone: m.senderPhone ?? m.sender ?? m.phoneNumber,
    messageText: m.messageText ?? m.content ?? m.text,
    tempId: m.tempId,
    createdAt: m.createdAt ?? m.timestamp,
  });

  // Lấy/khởi tạo session cho user
  useEffect(() => {
    if (!token) return;
    fetch("/api/chat/session", {
      headers: { Authorization:` Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((data) => setSessionId(data.id))
      .catch((err) => console.error("❌ Session error:", err));
  },[token]);

  // Auto scroll cuối danh sách
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Load lịch sử
  useEffect(() => {
    if (!sessionId || !token) return;
    fetch(`/api/chat/${sessionId}/messages`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((data) => {
        const list = (data || []).map(normalize).filter((m) => m.messageText?.trim());
        setMessages(list);
      })
      .catch((err) => console.error("❌ History error:", err));
  }, [sessionId, token]);

  // Kết nối STOMP
  useEffect(() => {
    if (!sessionId || !token) return;

    const sock = new SockJS(WS_URL);
    const stomp = new Client({
      webSocketFactory: () => sock,
      connectHeaders: { Authorization:` Bearer ${token}` },
      reconnectDelay: 1000, // ✅ tự reconnect
      onConnect: () => {
        // Subscribe room của session
        stomp.subscribe(`/topic/chat.${sessionId}`, (msg) => {
          const data = normalize(JSON.parse(msg.body));
          if (!data.messageText?.trim()) return;

          setMessages((prev) => {
            // Nếu server echo tempId → thay bản tạm bằng bản thật
            if (data.tempId) {
              const idx = prev.findIndex((x) => x.tempId && x.tempId === data.tempId);
              if (idx !== -1) {
                const copy = [...prev];
                copy[idx] = { ...data }; // thay luôn
                return copy;
              }
            }
            // Tránh trùng theo id
            if (data.id && prev.some((x) => x.id === data.id)) return prev;
            return [...prev, data];
          });
        });
      },
      onStompError: (f) => console.error("❌ STOMP error:", f.headers["message"], f.body),
      onWebSocketClose: () => console.warn("WebSocket closed"),
    });

    stomp.activate();
    clientRef.current = stomp;
    return () => {
      try { stomp.deactivate(); } catch {}
    };
  }, [sessionId, token]);

  const send = async () => {
    if (!input.trim() || !clientRef.current?.connected) return;

    const tempId = Date.now();
    const optimistic = {
      id: undefined,
      tempId,
      senderPhone: phoneNumber,
      messageText: input,
      sessionId,
    };
    setMessages((prev) => [...prev, optimistic]);

    // Publish kèm tempId để BE echo lại
    clientRef.current.publish({
      destination: "/app/chat.send",
      body: JSON.stringify({
        sessionId,
        content: input,
        phoneNumber,
        tempId, // ✅ quan trọng
      }),
    });

    setInput("");
  };

  return (
    <>
      <div className="chat-container">
        <div
          className="chat-icon"
          onClick={() => {
            if (!token || isExpired) setShowLoginModal(true);
            else setOpen(!open);
          }}
        >
          <MessageOutlined />
        </div>

        {token && (
          <div className={`chat-box ${open ? "active" : ""}`}>
            <div className="chat-header">Hỗ trợ trực tuyến</div>

            <div className="chat-body">
              {messages.map((m, i) => {
                const isUser = m.senderPhone === phoneNumber;
                return (
                  <div key={`${m.id ?? m.tempId ?? i}`} className={`chat-message ${isUser ? "user" : "support"}`}>
                    <div className="message-content">
                      {m.messageText}
                      {!m.id && m.tempId && <span className="sending-dot"> •••</span>}
                    </div>
                  </div>
                );
              })}
              <div ref={messagesEndRef} />
            </div>

            <div className="chat-footer">
              <input
                type="text"
                placeholder="Nhập tin nhắn..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && send()}
              />
              <button onClick={send}>Gửi</button>
            </div>
          </div>
        )}
      </div>

      <Modal
        open={showLoginModal}
        onCancel={() => setShowLoginModal(false)}
        footer={null}
        centered
        className="custom-modal-chat"
      >
        <h2>Thông báo</h2>
        <p>Bạn cần đăng nhập để tiếp tục. Bạn có muốn chuyển đến trang đăng nhập không?</p>
        <div className="modal-buttons">
          <button className="cancel-button" onClick={() => setShowLoginModal(false)}>HỦY</button>
          <button className="confirm-button" onClick={() => (window.location.href = "/login")}>ĐI ĐẾN ĐĂNG NHẬP</button>
        </div>
      </Modal>
    </>
  );
}

export default ChatRoom;