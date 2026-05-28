import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const AdminChat = () => {
  const [sessions, setSessions] = useState([]);
  const [selectedSession, setSelectedSession] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const messagesEndRef = useRef(null);

  // Kết nối WebSocket
  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/chat-websocket');
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log('Admin connected to WebSocket');
        
        // Subscribe để nhận tin nhắn từ tất cả users
        client.subscribe('/topic/admin', (message) => {
          const newMessage = JSON.parse(message.body);
          console.log('New message from user:', newMessage);
          
          // Cập nhật danh sách sessions
          setSessions(prev => {
            const existing = prev.find(s => s.id === newMessage.sessionId);
            if (!existing) {
              fetchSessions(); // Load lại sessions nếu có session mới
            }
            return prev;
          });

          // Nếu đang chat với session này, thêm tin nhắn vào
          if (selectedSession && selectedSession.id === newMessage.sessionId) {
            setMessages(prev => [...prev, newMessage]);
          }
        });

        fetchSessions();
      },
      onStompError: (frame) => {
        console.error('Admin connection error:', frame);
      }
    });

    client.activate();
    setStompClient(client);

    return () => {
      if (client) {
        client.deactivate();
      }
    };
  }, []);

  // Lấy danh sách sessions
  const fetchSessions = async () => {
    try {
      const response = await fetch('/api/chat/sessions');
      const data = await response.json();
      setSessions(data);
    } catch (error) {
      console.error('Lỗi tải sessions:', error);
    }
  };

  // Chọn session và load tin nhắn
  const selectSession = async (session) => {
    setSelectedSession(session);
    
    try {
      const response = await fetch(`/api/chat/sessions/${session.id}/messages`);
      const data = await response.json();
      setMessages(data);
    } catch (error) {
      console.error('Lỗi tải tin nhắn:', error);
    }
  };

  // Gửi tin nhắn đến user
  const sendMessage = () => {
    if (inputMessage.trim() && stompClient && selectedSession) {
      const message = {
        sessionId: selectedSession.id,
        senderPhone: 'Admin',
        content: inputMessage.trim()
      };
      
      stompClient.publish({
        destination: '/app/adminSend',
        body: JSON.stringify(message)
      });
      
      // Hiển thị tin nhắn ngay lập tức
      setMessages(prev => [...prev, {
        ...message,
        sentAt: new Date()
      }]);
      
      setInputMessage('');
    }
  };

  // Cuộn xuống tin nhắn mới nhất
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className="admin-chat-container">
      <div className="sessions-panel">
        <h3>Danh sách phiên chat ({sessions.length})</h3>
        <button onClick={fetchSessions} className="refresh-btn">🔄</button>
        
        <div className="sessions-list">
          {sessions.map(session => (
            <div 
              key={session.id} 
              className={`session-item ${selectedSession?.id === session.id ? 'active' : ''}`}
              onClick={() => selectSession(session)}
            >
              <div className="session-phone">{session.phoneNumber}</div>
              <div className="session-name">{session.fullName}</div>
              <div className="session-time">
                {new Date(session.createdAt).toLocaleDateString()}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="chat-panel">
        {selectedSession ? (
          <>
            <div className="chat-header">
              <h3>Chat với {selectedSession.fullName}</h3>
              <div className="session-info">
                <span>SĐT: {selectedSession.phoneNumber}</span>
                <span>Bắt đầu: {new Date(selectedSession.createdAt).toLocaleString()}</span>
              </div>
            </div>

            <div className="messages-container">
              {messages.map((msg, index) => (
                <div key={index} className={`message ${msg.senderPhone === 'Admin' ? 'admin-message' : 'user-message'}`}>
                  <div className="message-sender">{msg.senderPhone}</div>
                  <div className="message-content">{msg.content}</div>
                  <div className="message-time">
                    {msg.sentAt ? new Date(msg.sentAt).toLocaleTimeString() : 'Vừa xong'}
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>

            <div className="message-input">
              <input
                type="text"
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && sendMessage()}
                placeholder="Nhập tin nhắn cho user..."
              />
              <button onClick={sendMessage}>Gửi</button>
            </div>
          </>
        ) : (
          <div className="no-session">
            <p>Chọn một phiên chat từ danh sách bên trái</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminChat;