import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { FaUserCircle } from 'react-icons/fa';
import './AdminChat.scss'
const AdminChat = () => {
  const [sessions, setSessions] = useState([]);
  const [selectedSession, setSelectedSession] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [stompClient, setStompClient] = useState(null);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/chat-websocket');
    const client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('Admin connected to WebSocket');

        // nhận tin nhắn từ tất cả user
        client.subscribe('/topic/admin', (message) => {
          const newMessage = JSON.parse(message.body);
          console.log('New message from user:', newMessage);

          // cập nhật danh sách sessions: thêm mới hoặc move lên đầu
          setSessions(prev => {
            const existingIndex = prev.findIndex(s => s.id === newMessage.sessionId);
            let updated = [...prev];

            if (existingIndex !== -1) {
              const [session] = updated.splice(existingIndex, 1);
              updated.unshift(session);
            } else {
              fetchSessions().then(list => {
                const found = list.find(s => s.id === newMessage.sessionId);
                if (found) {
                  setSessions(prev2 => [found, ...prev2]);
                }
              });
            }
            return updated;
          });

          // nếu đang chat với session này thì append tin nhắn
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
      if (client) client.deactivate();
    };
  }, [selectedSession]);

  const fetchSessions = async () => {
    try {
      const response = await fetch('/api/chat/sessions', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
      const data = await response.json();
      if (Array.isArray(data)) {
        setSessions(data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
        return data;
      }
      return [];
    } catch (error) {
      console.error('Lỗi tải sessions:', error);
      return [];
    }
  };

  const selectSession = async (session) => {
    setSelectedSession(session);
    try {
      const response = await fetch(`/api/chat/sessions/${session.id}/messages`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
      const data = await response.json();
      setMessages(data);
    } catch (error) {
      console.error('Lỗi tải tin nhắn:', error);
    }
  };

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

      setMessages(prev => [...prev, {
        ...message,
        sentAt: new Date()
      }]);
      setInputMessage('');
    }
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <>
      <div className="chat-wrapper">
        {/* Bên trái: danh sách phiên chat */}
        <div className="sessions-panel">
          <div className="panel-header">
            <h3>Danh sách phiên chat ({sessions.length})</h3>
            {/* <button onClick={fetchSessions} className="refresh-btn">🔄</button> */}
          </div>

          <div className="sessions-list">
            {sessions.map(session => (
              <div
                key={session.id}
                className={`session-item ${selectedSession?.id === session.id ? 'active' : ''}`}
                onClick={() => selectSession(session)}
              >
                <FaUserCircle className="session-avatar" />
                <div className="session-texts">
                  <div className="session-name">{session.fullName || 'Guest'}</div>
                  <div className="session-last">
                    Bạn: {session.lastMessage || 'Chưa có tin nhắn'} ·{' '}
                    {session.createdAt ? new Date(session.createdAt).toLocaleTimeString() : ''}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Bên phải: chat */}
        <div className="chat-panel">
          {selectedSession ? (
            <>
              {/* Header */}
              <div className="chat-header">
                <FaUserCircle className="chat-avatar" />
                <div className="chat-header-info">
                  <div className="chat-name">{selectedSession.fullName || 'Guest'}</div>
                  <div className="chat-subinfo">
                    SĐT: {selectedSession.phoneNumber} · Bắt đầu:{' '}
                    {new Date(selectedSession.createdAt).toLocaleString()}
                  </div>
                </div>
              </div>

              {/* Tin nhắn */}
              <div className="messages-container">
                {messages.map((msg, index) => (
                  <div
                    key={index}
                    className={`message-row ${msg.senderPhone === 'Admin' ? 'admin-row' : 'user-row'}`}
                  >
                    {msg.senderPhone !== 'Admin' && <FaUserCircle className="msg-avatar" />}
                    <div className={`message-bubble ${msg.senderPhone === 'Admin' ? 'admin-bubble' : 'user-bubble'}`}>
                      <div className="message-content">{msg.content}</div>
                      <div className="message-time">
                        {msg.sentAt ? new Date(msg.sentAt).toLocaleTimeString() : 'Vừa xong'}
                      </div>
                    </div>
                    {msg.senderPhone === 'Admin' && <FaUserCircle className="msg-avatar" />}
                  </div>
                ))}
                <div ref={messagesEndRef} />
              </div>

              {/* Input */}
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
    </>
  );
};

export default AdminChat;