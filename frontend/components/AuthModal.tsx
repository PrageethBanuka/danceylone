"use client";

import { useState, useEffect } from "react";
import { register, login } from "@/lib/api";
import { toast } from "@/lib/toast";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

interface AuthModalProps {
  mode: "login" | "register";
  onClose: () => void;
  onSwitchMode: (mode: "login" | "register") => void;
}

export default function AuthModal({ mode, onClose, onSwitchMode }: AuthModalProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // Reset form when mode changes
  useEffect(() => {
    setEmail("");
    setPassword("");
    setFirstName("");
    setLastName("");
    setError("");
  }, [mode]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (mode === "register") {
        // Validate registration fields
        if (!firstName.trim() || !lastName.trim()) {
          setError("First name and last name are required");
          setLoading(false);
          return;
        }
        if (password.length < 8) {
          setError("Password must be at least 8 characters");
          setLoading(false);
          return;
        }
        
        await register({ email, password, firstName, lastName });
        toast.success("Registration successful!", "Please login to continue");
        onSwitchMode("login");
      } else {
        const response = await login({ email, password });
        localStorage.setItem("authToken", response.token);
        localStorage.setItem("token", response.token);
        localStorage.setItem("userEmail", response.email);
        localStorage.setItem("user", JSON.stringify({
          email: response.email,
          firstName: response.firstName,
          lastName: response.lastName,
          roles: response.roles
        }));
        toast.success("Welcome back!", `Logged in as ${response.email}`);
        window.location.reload();
      }
    } catch (err: any) {
      setError(err.response?.data?.message || "An error occurred. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={true} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md bg-white">
        <DialogHeader>
          <DialogTitle className="text-2xl font-semibold text-black">
            {mode === "login" ? "Sign In" : "Create Account"}
          </DialogTitle>
        </DialogHeader>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4 mt-4">
          {mode === "register" && (
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName" className="text-sm font-medium text-neutral-700">
                  First Name <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="firstName"
                  type="text"
                  required
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  className="border-neutral-300 focus:border-black focus:ring-black"
                  placeholder="John"
                  autoComplete="given-name"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName" className="text-sm font-medium text-neutral-700">
                  Last Name <span className="text-red-500">*</span>
                </Label>
                <Input
                  id="lastName"
                  type="text"
                  required
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  className="border-neutral-300 focus:border-black focus:ring-black"
                  placeholder="Doe"
                  autoComplete="family-name"
                />
              </div>
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="email" className="text-sm font-medium text-neutral-700">
              Email <span className="text-red-500">*</span>
            </Label>
            <Input
              id="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="border-neutral-300 focus:border-black focus:ring-black"
              placeholder="you@example.com"
              autoComplete="email"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password" className="text-sm font-medium text-neutral-700">
              Password <span className="text-red-500">*</span>
            </Label>
            <Input
              id="password"
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="border-neutral-300 focus:border-black focus:ring-black"
              minLength={8}
              placeholder="••••••••"
              autoComplete={mode === "register" ? "new-password" : "current-password"}
            />
            {mode === "register" && (
              <p className="text-xs text-neutral-500 mt-1">
                At least 8 characters with uppercase, lowercase, and a number
              </p>
            )}
          </div>

          <Button
            type="submit"
            disabled={loading}
            className="w-full bg-black text-white hover:bg-neutral-800 h-11 rounded-lg font-medium"
          >
            {loading ? "Processing..." : mode === "login" ? "Sign In" : "Create Account"}
          </Button>
        </form>

        <div className="mt-4 text-center text-sm text-neutral-600">
          {mode === "login" ? (
            <>
              Don't have an account?{" "}
              <button
                type="button"
                onClick={() => onSwitchMode("register")}
                className="text-black font-medium hover:underline"
              >
                Sign Up
              </button>
            </>
          ) : (
            <>
              Already have an account?{" "}
              <button
                type="button"
                onClick={() => onSwitchMode("login")}
                className="text-black font-medium hover:underline"
              >
                Sign In
              </button>
            </>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}
