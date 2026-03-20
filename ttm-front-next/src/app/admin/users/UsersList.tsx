'use client';
import { assignRole, getUsers } from "@/axios/UsersApis";
import { BackendUser } from "@/types/project";
import AssignmentAddIcon from '@mui/icons-material/AssignmentAdd';
import Image from "next/image";
import { useEffect, useState } from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableHeader,
    TableRow,
} from "../../../components/ui/table";

import CloseIcon from '@mui/icons-material/Close';
import Backdrop from '@mui/material/Backdrop';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import IconButton from '@mui/material/IconButton';



interface User {
    firstName: string;
    lastName: string;
    username: string;
    department: string;
    roles: string[];
    email: string;
}

export default function UsersList() {
    const [users, setUsers] = useState<User[]>([]); // State to store users
    const [searchTerm, setSearchTerm] = useState(""); // State for search input
    const [open, setOpen] = useState(true);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [popupOpen, setPopupOpen] = useState(false);
    const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const availableRoles = ["OWNER", "SPOC", "INTERLOCUTEUR_SIGNALE_IMPACT	", "INTERLOCUTEUR_RETOUR_IMPACT"];

    const handleClose = () => {
        setOpen(false);
    };


    // Fetch users from the API
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const backendUsers = await getUsers();

                const mappedUsers: User[] = backendUsers.map((data: BackendUser) => ({
                    firstName: data.firstName,
                    lastName: data.lastName,
                    username: data.username,
                    department: data.department,
                    roles: (data.roles || []).filter((role: string) =>
                        ["ADMIN", "OWNER", "SPOC", "INTERLOCUTEUR_SIGNALE_IMPACT", "INTERLOCUTEUR_RETOUR_IMPACT"].includes(role)
                    ),
                    email: data.email,
                }));

                setUsers(mappedUsers);
            } catch (error) {
                console.error("Error fetching users:", error);
            }
        };

        fetchUsers();
    }, []);

    // Filter users based on the search term
    const filteredUsers = users.filter((user) =>
        user.username.toLowerCase().includes(searchTerm.toLowerCase())
    );

    // Handle opening the role assignment popup
    const handleOpenPopup = (user: User) => {
        setSelectedUser(user);
        setSelectedRoles(user.roles || []);
        setPopupOpen(true);
    };

    // Handle closing the popup
    const handleClosePopup = () => {
        setPopupOpen(false);
        setSelectedUser(null);
    };

    // Handle role selection - only one role can be selected at a time
    const handleRoleChange = (role: string) => {
        // If clicking the same role that's already selected, do nothing
        if (selectedRoles.length === 1 && selectedRoles[0] === role) {
            return;
        }

        // Otherwise set only the clicked role
        setSelectedRoles([role]);
    };

    // Handle save roles - with calling the assignRole API 
    const handleSaveRoles = async () => {
        if (selectedUser && selectedRoles.length > 0) {
            try {
                setIsSubmitting(true);

                // Object to send to the API
                const payload = {
                    username: selectedUser.username,
                    roleName: selectedRoles[0],
                };

                // Call the API
                const response = await assignRole(payload);

                // If successful update user's role
                if (response.status === 200 || response.status === 201) {
                    setUsers(prevUsers =>
                        prevUsers.map(user =>
                            user.username === selectedUser.username
                                ? { ...user, roles: selectedRoles }
                                : user
                        )
                    );

                    console.log(`Role assigned successfully for ${selectedUser.username}:`, selectedRoles);
                } else {
                    console.error('Error assigning role: ', response);
                }
            } catch (error) {
                console.error('Error calling API: ', error);
                // Vous pourriez ajouter une notification d'erreur ici
            } finally {
                setIsSubmitting(false);
                handleClosePopup();
            }
        } else {
            console.warn('No user or role selected');
        }
    };

    if (!users) {
        return <div>
            <Backdrop
                sx={(theme) => ({ color: '#ab3c73', backgroundColor: '#FFF', zIndex: theme.zIndex.drawer + 1 })}
                open={open}
                onClick={handleClose}
            >
                <CircularProgress color="inherit" />
            </Backdrop>
        </div>;
    }

    return (
        <div className="pt-12 pb-32 pr-12 pl-12">
            {/* Search Bar */}
            <div className="relative right-4 w-2/5">
                <div className="p-4 flex items-center">
                    <input
                        type="text"
                        placeholder="Search for users..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="bg-white w-full px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-blue-500 text-sm dark:bg-white/[0.05] dark:text-white"
                    />
                    <Image
                        src={"/images/icons/search.png"}
                        alt="search-bar"
                        width={40}
                        height={40}
                        className="w-4 h-4 relative right-8"
                    />
                </div>
            </div>

            <div className="overflow-hidden rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.03]">
                {/* Table */}
                <div className="w-full overflow-x-auto">
                    <div className="min-w-[950px] mx-auto">
                        <Table>
                            <TableHeader className="border-b border-gray-100 dark:border-white/[0.05]">
                                <TableRow className="bg-gray-50 dark:bg-white/[0.03]">
                                    {["Full name", "Username", "Department", "Email", "Roles", "Actions"].map((head, idx) => (
                                        <TableCell
                                            key={idx}
                                            isHeader
                                            className="px-4 py-3 font-semibold text-gray-600 text-sm text-start dark:text-gray-400"
                                        >
                                            {head}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            </TableHeader>

                            <TableBody className="divide-y divide-gray-100 dark:divide-white/[0.05]">
                                {filteredUsers.map((user) => (
                                    <TableRow key={user.username} className="hover:bg-gray-50 dark:hover:bg-white/[0.05]">
                                        {/* Full name */}
                                        <TableCell className="px-4 py-4 text-nowrap text-start text-gray-700 text-sm dark:text-gray-400">
                                            {user.lastName} {user.firstName}
                                        </TableCell>

                                        {/* Username */}
                                        <TableCell className="px-4 py-4 text-nowrap text-start text-gray-700 text-sm dark:text-gray-400">
                                            {user.username}
                                        </TableCell>

                                        {/* Department */}
                                        <TableCell className="px-4 py-4 text-nowrap text-start text-gray-700 text-sm dark:text-gray-400">
                                            {user.department}
                                        </TableCell>

                                        {/* Email */}
                                        <TableCell className="px-4 py-4 text-nowrap text-start text-gray-700 text-sm dark:text-gray-400">
                                            {user.email}
                                        </TableCell>

                                        {/* Roles */}
                                        <TableCell className="px-4 pr-12 py-4 text-nowrap text-start text-gray-700 text-[12px] dark:text-gray-400">
                                            {user.roles && user.roles.length > 0 ? (
                                                <div className="px-3 relative right-3 bg-[#f5f3f3] w-fit border border-[#dcdcdc] rounded-[20px]">
                                                    {user.roles.join(', ')}
                                                </div>
                                            ) : (
                                                <span className="text-gray-400">No roles</span>
                                            )}
                                        </TableCell>

                                        {/* Action */}
                                        <TableCell className="px-4 pl-8 py-4 text-nowrap">
                                            <IconButton
                                                className="cursor-pointer"
                                                onClick={() => handleOpenPopup(user)}
                                            >
                                                <AssignmentAddIcon />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                </div>
            </div>

            {/* Role Assignment Popup - Utilisation de ThemeProvider uniquement pour la popup */}
            <Dialog
                style={{ marginTop: '2.5rem' }}
                open={popupOpen}
                onClose={handleClosePopup}
                maxWidth="md"
                fullWidth
                PaperProps={{
                    style: {
                        borderRadius: '12px',
                        border: '1px solid #e0e0e0',
                        boxShadow: '0 8px 24px rgba(0, 0, 0, 0.12)'
                    }
                }}
            >
                <DialogTitle sx={{ bgcolor: '#f9f5f7', color: '#ab3c73', fontWeight: 'bold', borderBottom: '1px solid #e0e0e0' }}>
                    Assign Roles
                    <IconButton
                        aria-label="close"
                        onClick={handleClosePopup}
                        sx={{
                            position: 'absolute',
                            right: 8,
                            top: 8,
                            color: '#ab3c73',
                        }}
                    >
                        <CloseIcon />
                    </IconButton>
                </DialogTitle>
                <DialogContent dividers sx={{ padding: '24px' }}>
                    {selectedUser && (
                        <div>
                            <div className="mb-4 py-4 px-8 flex justify-start gap-6 w-full items-center content-center  bg-[#f9f5f7] rounded-lg border border-[#e0e0e0]">
                                <p className="font-semibold text-[#ab3c73]">Full name : <span className="font-normal text-gray-700">{selectedUser.lastName} {selectedUser.firstName}</span></p>
                                <p className="font-semibold text-[#ab3c73] ">Direction : <span className="font-normal text-gray-700">{selectedUser.department}</span></p>
                                <p className="font-semibold text-[#ab3c73] ">Default role : <span className="font-normal text-gray-700">{selectedUser.roles.length > 0 ? (
                                    <span>  {selectedUser.roles.join(', ').toLowerCase()} </span>
                                ) : (
                                    <span className="text-gray-700">No roles yet</span>
                                )}</span></p>
                            </div>

                            <div className="mt-6 p-4 bg-white rounded-lg border border-[#e0e0e0]">
                                <p className="font-semibold mb-3 text-[#ab3c73]">Select Role :</p>
                                <FormGroup>
                                    {availableRoles.map((role) => (
                                        <FormControlLabel
                                            key={role}
                                            control={
                                                <Checkbox
                                                    sx={{
                                                        color: '#d4a6bb',
                                                        '&.Mui-checked': {
                                                            color: '#ab3c73',
                                                        },
                                                    }}
                                                    checked={selectedRoles.includes(role)}
                                                    onChange={() => handleRoleChange(role)}
                                                />
                                            }
                                            label={<span className=" text-sm text-gray-700">{role}</span>}
                                        />
                                    ))}
                                </FormGroup>
                            </div>
                        </div>
                    )}
                </DialogContent>
                <div className="flex justify-end gap-4 py-2 px-8 bg-[#f9f5f7] border-t border-[#e0e0e0]">
                    <Button
                        onClick={handleClosePopup}
                        sx={{
                            color: '#ab3c73',
                            '&:hover': {
                                backgroundColor: '#f9f5f7'
                            }
                        }}
                    >
                        Cancel
                    </Button>
                    <Button
                        onClick={handleSaveRoles}
                        variant="contained"
                        disabled={isSubmitting}
                        sx={{
                            backgroundColor: '#ab3c73',
                            '&:hover': {
                                backgroundColor: '#8e3260'
                            }
                        }}
                    >
                        {isSubmitting ? 'Saving...' : 'Save'}
                    </Button>
                </div>
            </Dialog>
        </div>
    );
}