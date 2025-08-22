import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import { MessageOutlined, CloseOutlined, } from "@ant-design/icons";
import SockJS from 'sockjs-client';
import { FaUserCircle } from "react-icons/fa";

import './UserChat.scss';

const UserChat = () => {
  const [userInfo, setUserInfo] = useState({ fullName: '', phone: '', gender: 'Anh' });
  const [isConnected, setIsConnected] = useState(false);
  const [isOpen, setIsOpen] = useState(false); // ✅ điều khiển popup
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const [mySession, setMySession] = useState(null);
  const messagesEndRef = useRef(null);

  const connect = () => {
    if (!userInfo.fullName || !userInfo.phone) {
      alert('Vui lòng nhập đầy đủ họ tên và số điện thoại!');
      return;
    }

    const socket = new SockJS('http://localhost:8080/chat-websocket');
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: async () => {
        setIsConnected(true);
        console.log('Connected to WebSocket');

        // gửi message để tạo session
        const firstMessage = {
          senderPhone: userInfo.phone,
          content: `${userInfo.gender} ${userInfo.fullName}`
        };
        client.publish({
          destination: '/app/sendMessage',
          body: JSON.stringify(firstMessage)
        });

        try {
          const res = await fetch(`/api/chat/sessions`, {
            headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
          });
          const sessions = await res.json();
          const mySess = sessions.find(s => s.phoneNumber === userInfo.phone);

          if (mySess) {
            setMySession(mySess);
            client.subscribe(`/topic/session/${mySess.id}`, (message) => {
              const newMsg = JSON.parse(message.body);
              setMessages(prev => [...prev, newMsg]);
            });
          }
        } catch (err) {
          console.error("Không lấy được sessionId:", err);
        }
      }
    });

    client.activate();
    setStompClient(client);
  };
  const sendMessage = () => {
  if (!stompClient || !stompClient.connected) {
    console.warn("⚠️ Chưa có kết nối STOMP, thử gửi lại sau");
    return;
  }

  if (inputMessage.trim() && mySession) {
    const message = {
      sessionId: mySession.id,
      senderPhone: userInfo.phone,
      content: inputMessage.trim()
    };

    stompClient.publish({
      destination: '/app/sendMessage',
      body: JSON.stringify(message)
    });

    setMessages(prev => [...prev, { ...message, sentAt: new Date() }]);
    setInputMessage('');
  }
};




  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    return () => {
      if (stompClient) stompClient.deactivate();
    };
  }, [stompClient]);

  return (
    <>
      {/* Floating Icon */}
      {!isOpen && (
        <div className="chat-floating-icon" onClick={() => setIsOpen(true)}>
          <MessageOutlined style={{ fontSize: 28, color: "white" }} />
        </div>
      )}

      {/* Popup Chat */}
      {isOpen && (
        <div className="chat-popup">
          <div className="chat-popup-header">
            <CloseOutlined className="chat-close" onClick={() => setIsOpen(false)} />
            <h3>Hỗ trợ khách hàng</h3>
          </div>

          {!isConnected ? (
            <div className="user-info-form">
              <h4>Nhập thông tin để tiện hỗ trợ</h4>
              <input
                type="text"
                placeholder="Họ và tên"
                value={userInfo.fullName}
                onChange={(e) => setUserInfo({ ...userInfo, fullName: e.target.value })}
              />
              <input
                type="tel"
                placeholder="Số điện thoại"
                value={userInfo.phone}
                onChange={(e) => setUserInfo({ ...userInfo, phone: e.target.value })}
              />
              <select
                value={userInfo.gender}
                onChange={(e) => setUserInfo({ ...userInfo, gender: e.target.value })}
              >
                <option value="Anh">Anh</option>
                <option value="Chị">Chị</option>
              </select>
              <div className="form-actions">
                <button className="btn-start" onClick={connect}>BẮT ĐẦU CHAT</button>
                <button className="btn-cancel" onClick={() => setIsOpen(false)}>HỦY</button>
              </div>
            </div>
          ) : (
            <>
              <div className="messages-container">
                {messages.map((msg, index) => {
                  const isOwn = msg.senderPhone === userInfo.phone;
                  return (
                    <div
                      key={index}
                      className={`message-row ${isOwn ? 'message-right' : 'message-left'}`}
                    >
                      {/* Nếu là Admin thì hiện avatar bên trái */}
                      {!isOwn && <FaUserCircle className="msg-avatar" />}

                      <div className="message-box">
                        <div className="message-text">{msg.content}</div>
                        <div className="message-time">
                          {msg.sentAt ? new Date(msg.sentAt).toLocaleTimeString() : 'Vừa xong'}
                        </div>
                      </div>

                      {/* Nếu là User thì hiện avatar bên phải */}
                      {isOwn && <FaUserCircle className="msg-avatar user-avatar" />}
                    </div>
                  );
                })}
                <div ref={messagesEndRef} />
              </div>


              <div className="message-input">
                <input
                  type="text"
                  value={inputMessage}
                  onChange={(e) => setInputMessage(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
                  placeholder="Nhập tin nhắn..."
                />
                <button onClick={sendMessage}>Gửi</button>
              </div>
            </>
          )}
        </div>
      )}
    </>
  );
};

export default UserChat;
