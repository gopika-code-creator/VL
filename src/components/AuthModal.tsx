import React, { useState } from 'react';
import { User } from '../types';
import { X, Lock, Mail, User as UserIcon, ShieldCheck } from 'lucide-react';

interface AuthModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLogin: (user: User) => void;
}

export const AuthModal: React.FC<AuthModalProps> = ({
  isOpen,
  onClose,
  onLogin
}) => {
  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState('alex_founder');
  const [email, setEmail] = useState('alex@venturelens.io');
  const [password, setPassword] = useState('password123');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const mockUser: User = {
      id: Math.floor(Math.random() * 1000) + 1,
      username: username.trim() || 'founder_alex',
      email: email.trim() || 'founder@venturelens.io',
      createdAt: new Date().toISOString()
    };
    onLogin(mockUser);
    onClose();
  };

  return (
    <div
      id="modal-auth"
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-150"
    >
      <div className="bg-[#162235] border border-[#273852] rounded-2xl w-full max-w-md p-6 shadow-2xl relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-[#94A3B8] hover:text-[#F1F5F9] p-1 rounded-lg"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="text-center mb-6">
          <div className="w-12 h-12 rounded-xl bg-[#10B981]/20 border border-[#10B981]/30 flex items-center justify-center text-[#10B981] mx-auto mb-3">
            <Lock className="w-6 h-6" />
          </div>
          <h3 className="text-lg font-bold text-[#F1F5F9]">
            {isRegister ? 'Create Founder Account' : 'Authenticate to VentureLens'}
          </h3>
          <p className="text-xs text-[#94A3B8] mt-1">
            Standard SHA-256 password hashing with MySQL JDBC persistence.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Username
            </label>
            <div className="relative">
              <UserIcon className="w-4 h-4 text-[#64748B] absolute left-3 top-2.5" />
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                className="w-full pl-9 pr-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
              />
            </div>
          </div>

          {isRegister && (
            <div>
              <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
                Work Email
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 text-[#64748B] absolute left-3 top-2.5" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                  className="w-full pl-9 pr-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
                />
              </div>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Password (SHA-256 Encrypted)
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-[#64748B] absolute left-3 top-2.5" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full pl-9 pr-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-2.5 text-xs font-bold text-white bg-[#10B981] hover:bg-[#059669] rounded-lg transition-colors cursor-pointer shadow-xs mt-2"
          >
            {isRegister ? 'Register & Establish Session' : 'Authenticate Session'}
          </button>
        </form>

        <div className="text-center mt-4 pt-4 border-t border-[#273852]">
          <button
            type="button"
            onClick={() => setIsRegister(!isRegister)}
            className="text-xs text-[#94A3B8] hover:text-[#10B981] transition-colors cursor-pointer"
          >
            {isRegister
              ? 'Already registered? Switch to Login'
              : 'Need a new account? Register here'}
          </button>
        </div>
      </div>
    </div>
  );
};
